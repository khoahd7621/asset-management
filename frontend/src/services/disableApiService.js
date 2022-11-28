import axios from '../utils/customAxios';

const checkValid = (staffCode) => {
  return axios.get(`/api/user/check-user?staffCode=${staffCode}`)
}

const disableUser = (staffCode) => {
  return axios.delete(`/api/user?staffCode=${staffCode}`);
};

export {disableUser, checkValid} 
