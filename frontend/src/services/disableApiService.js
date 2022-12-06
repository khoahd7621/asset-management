import axios from '../utils/customAxios';

const checkValid = (staffCode) => {
  return axios.get(`/api/user/check-user?staffCode=${staffCode}`);
};

const disableUser = (staffCode) => {
  return axios.delete(`/api/user?staffCode=${staffCode}`);
};

const deleteAsset = (assetId) => {
  return axios.delete(`/api/asset/${assetId}`);
};

const deleteAssignment = (assignmentId) => {
  return axios.delete(`/api/assignment?assignmentId=${assignmentId}`);
};

export { disableUser, checkValid, deleteAsset, deleteAssignment };
