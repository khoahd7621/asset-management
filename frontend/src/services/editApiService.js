import axios from '../utils/customAxios';

const putEditAsset = ({ id, assetName, specification, installedDate, assetStatus }) => {
  return axios.put(`/api/asset/${id}`, {
    assetName,
    specification,
    installedDate,
    assetStatus,
  });
};

export { putEditAsset };
