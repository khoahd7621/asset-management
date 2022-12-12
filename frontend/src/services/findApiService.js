import axios from '../utils/customAxios';

export const searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination = ({
  keyWord,
  statuses,
  categories,
  limit,
  page,
  sortField,
  sortType,
}) => {
  return axios.get(
    '/api/asset?' +
      `key-word=${keyWord}&` +
      `statuses=${statuses}&` +
      `categories=${categories}&` +
      `limit=${limit}&` +
      `page=${page}&` +
      `sort-field=${sortField}&` +
      `sort-type=${sortType}`,
  );
};

export const filterAssignmentList = (name, statuses, assigndate, page) => {
  return axios.get(`/api/assignment?name=${name}&status=${statuses}&date=${assigndate}&page=${page}`);
};

export const searchUsersWithKeywordAndTypesWithPagination = ({ keyWord, types, limit, page, sortField, sortType }) => {
  return axios.get(
    '/api/user/search?' +
      `key-word=${keyWord}&` +
      `types=${types}&` +
      `limit=${limit}&` +
      `page=${page}&` +
      `sort-field=${sortField}&` +
      `sort-type=${sortType}`,
  );
};

export const filterRequestList = ({ query, statuses, date, page }) => {
  return axios.get(`/api/return-asset?query=${query}&statuses=${statuses}&date=${date}&page=${page}`);
};
