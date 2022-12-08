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
  return axios.put('api/user/change-password/first', {
    newPassword,
  });
};

const putEditAssignment = (assignmentId, { assetId, userId, assignedDate, note }) => {
  return axios.put(`/api/assignment/${assignmentId}`, {
    assetId,
    userId,
    assignedDate,
    note,
  });
};

const putAcceptAssignAsset = ({ idAccept }) => {
  return axios.put(`/api/assignment/user/accept/${idAccept}`);
};

const putDeclineAssignAsset = ({ idDecline }) => {
  return axios.put(`/api/assignment/user/decline/${idDecline}`);
};

const patchCompleteRequestReturnAsset = (returnAssetId) => {
  return axios.patch(`/api/return-asset?id=${returnAssetId}`);
};

export {
  postEditUser,
  putEditAsset,
  putChangePassword,
  putChangePasswordFirst,
  putEditAssignment,
  putAcceptAssignAsset,
  putDeclineAssignAsset,
  patchCompleteRequestReturnAsset
};
