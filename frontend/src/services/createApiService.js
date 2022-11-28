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

export { postCreateNewUser, postCreateNewCategory, postCreateNewAsset };
