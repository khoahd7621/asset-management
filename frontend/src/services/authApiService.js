import axios from '../utils/customAxios';

const postLogin = (username, password) => {
  return axios.post('api/login', {
    username,
    password,
  });
};

export { postLogin };