import axios from '../utils/customAxios';

export const getAllCategories = () => {
  return axios.get('/api/category');
};

export const getAssetDetailAndItsHistories = (assetId) => {
  return axios.get(`/api/asset/${assetId}`);
};
