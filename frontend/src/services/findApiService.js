import axios from '../utils/customAxios';

export const getItems = (url) => {
    return axios.get(url);
};