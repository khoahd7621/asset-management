import axios from 'axios';
import NProgress from 'nprogress';
import { store } from '../redux/store';

NProgress.configure({ showSpinner: false, trickleSpeed: 100 });

const instance = axios.create({
  baseURL: process.env.REACT_APP_BASE_API_HOST,
});

instance.interceptors.request.use(
  function (config) {
    let accessToken = store?.getState()?.user?.user?.accessToken;
    if (accessToken !== '') {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    NProgress.start();
    return config;
  },
  function (error) {
    NProgress.done();
    return Promise.reject(error);
  },
);

instance.interceptors.response.use(
  function (response) {
    NProgress.done();
    return response;
  },
  function (error) {
    NProgress.done();
    return error;
  },
);

export default instance;
