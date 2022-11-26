import axios from '../utils/customAxios';

export const getItems = (url) => {
  return axios.get(url);
};

export const filterAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination = ({
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
