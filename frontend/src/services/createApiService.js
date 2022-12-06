import axios from '../utils/customAxios';

const postCreateNewUser = ({ firstName, lastName, dateOfBirth, gender, joinedDate, type, location }) => {
  return axios.post('/api/user', {
    firstName,
    lastName,
    dateOfBirth,
    gender,
    joinedDate,
    type,
    location,
  });
};

const postCreateNewCategory = ({ categoryName, prefixAssetCode }) => {
  return axios.post('/api/category', {
    categoryName,
    prefixAssetCode,
  });
};

const postCreateNewAsset = ({ assetName, categoryName, specification, installedDate, assetStatus }) => {
  return axios.post('/api/asset', {
    assetName,
    categoryName,
    specification,
    installedDate,
    assetStatus,
  });
};

const createNewAssignment = ({ assetId, userId, assignedDate, note }) => {
  return axios.post('/api/assignment', {
    assetId,
    userId,
    assignedDate,
    note,
  });
};

const postCreateNewRequestReturn = ({ idRequest }) => {
  return axios.post('/api/return-asset?id=' + `${idRequest}`);
};

export {
  postCreateNewUser,
  postCreateNewCategory,
  postCreateNewAsset,
  createNewAssignment,
  postCreateNewRequestReturn,
};
