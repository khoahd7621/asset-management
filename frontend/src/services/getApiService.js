import axios from '../utils/customAxios';

export const getAllCategories = () => {
  return axios.get('/api/category');
};

export const getAssetDetailAndItsHistories = (assetId) => {
  return axios.get(`/api/asset/${assetId}`);
};

export const getAssignmentDetails = (assignmentId) => {
  return axios.get(`/api/assignment/details?id=${assignmentId}`);
};

export const getCheckAssetIsValidForDeleteOrNot = (assetId) => {
  return axios.get(`/api/asset/check-asset/${assetId}`);
};

export const getAllUsers = () => {
  return axios.get('/api/user');
};

export const getAllAssetsByStatus = (status) => {
  return axios.get(`/api/asset/status/${status}`);
};
