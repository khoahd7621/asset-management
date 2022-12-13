import axios from '../utils/customAxios';

export const getUserDetails = (staffCode) => {
  return axios.get(`/api/user/get/${staffCode}`);
};

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

export const getAllMyAssignAsset = () => {
  return axios.get(`/api/assignment/user`);
};

export const getAssignAssetDetails = (asignAssetId) => {
  return axios.get(`/api/assignment/user/${asignAssetId}`);
};

export const getReportDetails = () => {
  return axios.get('/api/report');
};

export const getCurrentUserLoggedInByUsername = (username) => {
  return axios.get(`/api/user/${username}/current`);
};
