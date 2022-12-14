import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Input, Space, Spin } from 'antd';

import './ManageAsset.scss';

import { FilterMenu, TableAsset } from '../../../components';
import { adminRoute } from '../../../routes/routes';
import { searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination } from '../../../services/findApiService';
import { getAllCategories } from '../../../services/getApiService';
import convertEnum from '../../../utils/convertEnumUtil';

const ManageAsset = () => {
  const location = useLocation();
  const { Search } = Input;
  const PAGE_SIZE = 20;
  const LIST_STATUS = ['All', 'Assigned', 'Available', 'Not available', 'Waiting for recycling', 'Recycled'];

  const [totalRow, setTotalRow] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [listAssets, setListAssets] = useState([]);
  const [currentChoosedStatus, setCurrentChoosedStates] = useState(['Assigned', 'Available', 'Not available']);
  const [listCategories, setListCategories] = useState([]);
  const [listCategoriesOption, setListCategoriesOption] = useState(['All']);
  const [currentChoosedCategories, setCurrentChoosedCategories] = useState(['All']);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = 'Manage Asset - Asset List';
  }, []);
  useEffect(() => {
    setIsLoading(true);
    fetchListCategories();
    fetchAssets(
      searchKeywords,
      convertListStatus(currentChoosedStatus),
      mapListCategoriesNameToListCategoriesId(currentChoosedCategories),
      PAGE_SIZE,
      currentPage - 1,
      '',
      '',
    );
  }, []);

  const fetchAssets = async (keyWord, statuses, categories, limit, page, sortField, sortType) => {
    const response = await searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination({
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
      setIsLoading(false);
      if (newAssetCreate) {
        const listDatas = response?.data?.data.filter((item) => item.assetCode !== newAssetCreate.assetCode);
        const listDataNew = [newAssetCreate, ...listDatas];
        setListAssets(
          listDataNew.map((item) => {
            return {
              key: item.assetCode,
              assetId: item.id,
              assetCode: item.assetCode,
              assetName: item.assetName.replaceAll(' ', '\u00a0'),
              category: item.category.name,
              state: convertEnum.toShow(item.status),
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
                  assetName: item.assetName.replaceAll(' ', '\u00a0'),
                  category: item.category.name,
                  state: convertEnum.toShow(item.status),
                };
              }),
        );
      }

      window.history.replaceState({}, document.title);
    }
  };

  const fetchListAssets = async (keyWord, statuses, categories, limit, page, sortField, sortType) => {
    const response = await searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination({
      keyWord,
      statuses,
      categories,
      limit,
      page,
      sortField,
      sortType,
    });
    if (response && response.status === 200) {
      setTotalRow(response?.data.totalRow);
      setListAssets(
        response?.data?.data.length === 0
          ? []
          : response?.data?.data.map((item) => {
              return {
                key: item.assetCode,
                assetId: item.id,
                assetCode: item.assetCode,
                assetName: item.assetName.replaceAll(' ', '\u00a0'),
                category: item.category.name,
                state: convertEnum.toShow(item.status),
              };
            }),
      );
    }
  };

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
        PAGE_SIZE,
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
        PAGE_SIZE,
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
      PAGE_SIZE,
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
      PAGE_SIZE,
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
          id="filter-asset-status"
          options={LIST_STATUS}
          value={currentChoosedStatus}
          onChange={handleChangeFilterMenu}
          title="State"
          checkboxType="ASSET_STATE"
        />
        <FilterMenu
          id="filter-asset-category"
          options={listCategoriesOption}
          value={currentChoosedCategories}
          title="Category"
          onChange={handleChangeFilterMenu}
          checkboxType="ASSET_CATEGORY"
        />
        <Search
          allowClear
          className="search-input"
          onSearch={handleSubmitSearch}
          enterButton
          id="manager-asset__search-asset"
        />
        <Link
          to={`/${adminRoute.home}/${adminRoute.manageAsset}/${adminRoute.createAsset}`}
          className="create-asset-btn"
        >
          Create new asset
        </Link>
      </div>
      <div className="manage-asset-block__table">
        {isLoading ? (
          <Space size="middle">
            <Spin size="large" style={{ paddingLeft: '30rem', paddingTop: '10rem' }} />
          </Space>
        ) : (
          <TableAsset
            currentPage={currentPage}
            listAssets={listAssets}
            totalRow={totalRow}
            pageSize={PAGE_SIZE}
            handleChangeCurrentPage={handleChangeCurrentPage}
            searchKeywords={searchKeywords}
            statuses={convertListStatus(currentChoosedStatus)}
            categories={mapListCategoriesNameToListCategoriesId(currentChoosedCategories)}
            fetchListAssets={fetchListAssets}
          />
        )}
      </div>
    </div>
  );
};

export default ManageAsset;
