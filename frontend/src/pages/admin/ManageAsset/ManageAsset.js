import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Input } from 'antd';

import './ManageAsset.scss';

import { FilterMenu, TableAsset } from '../../../components';
import { adminRoute } from '../../../routes/routes';
import { filterAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination } from '../../../services/findApiService';
import { getAllCategories } from '../../../services/getApiService';

const ManageAsset = () => {
  const location = useLocation();
  const { Search } = Input;
  const LIST_STATUS = ['All', 'Assigned', 'Available', 'Not available', 'Waiting for recycling', 'Recycled'];

  const [pageSize, setPageSize] = useState(20);
  const [totalRow, setTotalRow] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [listAssets, setListAssets] = useState([]);
  const [currentChoosedStatus, setCurrentChoosedStates] = useState(['Assigned', 'Available', 'Not available']);
  const [listCategories, setListCategories] = useState([]);
  const [listCategoriesOption, setListCategoriesOption] = useState(['All']);
  const [currentChoosedCategories, setCurrentChoosedCategories] = useState(['All']);
  const [searchKeywords, setSearchKeywords] = useState('');

  useEffect(() => {
    fetchListCategories();
    fetchListAssets(
      searchKeywords,
      convertListStatus(currentChoosedStatus),
      mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
      pageSize,
      currentPage - 1,
      '',
      '',
    );
  }, []);

  const fetchListAssets = async (keyWord, statuses, categories, limit, page, sortField, sortType) => {
    const response = await filterAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination({
      keyWord,
      statuses,
      categories,
      limit,
      page,
      sortField,
      sortType,
    });
    if (response && response.status === 200) {
      const newAssetCreate = location.state?.assetResponse;
      setTotalRow(response?.data.totalRow);

      if (newAssetCreate) {
        const listDatas = response?.data?.data.filter((item) => item.assetCode !== newAssetCreate.assetCode);
        const listDataNew = [newAssetCreate, ...listDatas];
        setListAssets(
          listDataNew.map((item) => {
            return {
              key: item.assetCode,
              assetId: item.id,
              assetCode: item.assetCode,
              assetName: item.assetName,
              category: item.category.name,
              state: capitalizeFirstLetter(item.status.toLowerCase().replaceAll('_', ' ')),
            };
          }),
        );
      } else {
        setListAssets(
          response?.data?.data.length === 0
            ? []
            : response?.data?.data.map((item) => {
                return {
                  key: item.assetCode,
                  assetId: item.id,
                  assetCode: item.assetCode,
                  assetName: item.assetName,
                  category: item.category.name,
                  state: capitalizeFirstLetter(item.status.toLowerCase().replaceAll('_', ' ')),
                };
              }),
        );
      }

      window.history.replaceState({}, document.title);
    }
  };

  const capitalizeFirstLetter = (string) => string.charAt(0).toUpperCase() + string.slice(1);

  const convertListStatus = (listStatus) => {
    if (listStatus.length === 1 && listStatus[0] === 'All') {
      return [];
    }
    return listStatus.map((item) => item.toUpperCase().replaceAll(' ', '_'));
  };

  const mapListCategoriesNameToListCategoriesId = (listCategoriesName) => {
    if (listCategoriesName.length === 1 && listCategoriesName[0] === 'All') {
      return [];
    }
    return listCategoriesName.map((categoryName) => listCategories.find((item) => item.name === categoryName).id);
  };

  const fetchListCategories = async () => {
    const response = await getAllCategories();
    if (response && response.status === 200) {
      setListCategoriesOption(response.data.reduce((prev, curr) => [...prev, curr.name], ['All']));
      setListCategories(response.data);
    }
  };

  const handleChangeFilterMenu = (selection, type) => {
    if (type === 'ASSET_STATE') {
      setCurrentChoosedStates(selection);
      setCurrentPage(1);
      fetchListAssets(
        searchKeywords,
        convertListStatus(selection),
        mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
        pageSize,
        0,
        '',
        '',
      );
    }
    if (type === 'ASSET_CATEGORY') {
      setCurrentChoosedCategories(selection);
      setCurrentPage(1);
      fetchListAssets(
        searchKeywords,
        convertListStatus(currentChoosedStatus),
        mapListCategoriesNameToListCategoriesId(selection),
        pageSize,
        0,
        '',
        '',
      );
    }
  };

  const handleSubmitSearch = (value) => {
    setSearchKeywords(value);
    setCurrentPage(1);
    fetchListAssets(
      value,
      convertListStatus(currentChoosedStatus),
      mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
      pageSize,
      0,
      '',
      '',
    );
  };

  const handleChangeCurrentPage = (current) => {
    setCurrentPage(current);
    fetchListAssets(
      searchKeywords,
      convertListStatus(currentChoosedStatus),
      mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
      pageSize,
      current - 1,
      '',
      '',
    );
  };

  const handleChangePageSize = (current, size) => {
    setCurrentPage(current);
    setPageSize(size);
    fetchListAssets(
      searchKeywords,
      convertListStatus(currentChoosedStatus),
      mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
      size,
      current - 1,
      '',
      '',
    );
  };

  return (
    <div className="manage-asset-block">
      <div className="manage-asset-block__title">Asset List</div>
      <div className="manage-asset-block__action">
        <FilterMenu
          options={LIST_STATUS}
          value={currentChoosedStatus}
          onChange={handleChangeFilterMenu}
          title="State"
          checkboxType="ASSET_STATE"
        />
        <FilterMenu
          options={listCategoriesOption}
          value={currentChoosedCategories}
          title="Category"
          onChange={handleChangeFilterMenu}
          checkboxType="ASSET_CATEGORY"
        />
        <Search allowClear className="search-input" onSearch={handleSubmitSearch} enterButton />
        <Link
          to={`/${adminRoute.home}/${adminRoute.manageAsset}/${adminRoute.createAsset}`}
          className="create-asset-btn"
        >
          Create new asset
        </Link>
      </div>
      <div className="manage-asset-block__table">
        <TableAsset
          currentPage={currentPage}
          listAssets={listAssets}
          totalRow={totalRow}
          pageSize={pageSize}
          handleChangeCurrentPage={handleChangeCurrentPage}
          handleChangeSizePage={handleChangePageSize}
        />
      </div>
    </div>
  );
};

export default ManageAsset;
