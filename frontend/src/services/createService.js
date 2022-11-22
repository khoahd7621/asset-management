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

export { postCreateNewUser };
