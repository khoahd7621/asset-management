import axios from '../utils/customAxios';

const postEditUser = ({ dateOfBirth, gender, joinedDate, type, staffCode }) => {
  return axios.put('api/user/edit', {
    dateOfBirth,
    gender,
    joinedDate,
    type,
    staffCode,
  });
};

const putEditAsset = ({ id, assetName, specification, installedDate, assetStatus }) => {
  return axios.put(`/api/asset/${id}`, {
    assetName,
    specification,
    installedDate,
    assetStatus,
  });
};

const putChangePassword = ({ oldPassword, newPassword }) => {
  return axios.put('api/user/change-password', {
    oldPassword,
    newPassword,
  });
};

const putChangePasswordFirst = ({ newPassword }) => {
  return axios.put('api/user/change-password-first', {
    newPassword,
  });
};

export { postEditUser, putEditAsset, putChangePassword, putChangePasswordFirst };
