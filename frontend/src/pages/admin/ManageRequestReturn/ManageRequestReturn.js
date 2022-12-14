import { useEffect, useState } from 'react';
import { DatePicker, Input, Space, Spin } from 'antd';

import './ManageRequestReturn.scss';

import { FilterMenu } from '../../../components';
import TableRequest from '../../../components/TableRequest/TableRequest';
import { filterRequestList } from '../../../services/findApiService';
import convertEnum from '../../../utils/convertEnumUtil';

const ManageRequestReturn = () => {
  const { Search } = Input;
  const PAGE_SIZE = 20;
  const LIST_STATUS = ['All', 'Completed', 'Waiting for returning'];

  const [totalRow, setTotalRow] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [listAssets, setListAssets] = useState([]);
  const [currentChoosedStatus, setCurrentChoosedStates] = useState(['All']);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [date, setDate] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = 'Request For Returning - Request List';
  }, []);

  useEffect(() => {
    setIsLoading(true);
    fetchListRequest(searchKeywords, convertListStatus(currentChoosedStatus), date, currentPage - 1);
  }, []);

  useEffect(() => {
    fetchListRequest(searchKeywords, convertListStatus(currentChoosedStatus), date, currentPage - 1);
  }, [currentChoosedStatus, searchKeywords, currentPage, date]);

  const fetchListRequest = async (keyWord, statuses, date, page) => {
    const response = await filterRequestList({
      query: keyWord,
      statuses: statuses,
      date: date,
      page: page,
    });
    if (response && response.status === 200) {
      let count = (currentPage - 1) * PAGE_SIZE;
      setTotalRow(response?.data.totalRow);
      setIsLoading(false);
      setListAssets(
        response?.data?.data.length === 0
          ? []
          : response?.data?.data.map((item) => {
              count++;
              return {
                no: count,
                key: item.id,
                assetId: item.id,
                assetCode: item.assetCode,
                assetName: item.assetName.replaceAll(' ', '\u00a0'),
                requestBy: item.requestedByUser,
                acceptedBy: item.acceptByUser,
                returnDate: item.returnedDate,
                date: item.assignedDate,
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

  const handleChangeFilterMenu = (selection, type) => {
    if (type === 'ASSET_STATE') {
      setCurrentChoosedStates(selection);
      setCurrentPage(1);
    }
  };

  const handleValidateDateOfBirth = (date, _dateString) => {
    if (date) {
      setCurrentPage(1);
      setDate(date.format('DD/MM/YYYY'));
    } else {
      setCurrentPage(1);
      setDate('');
    }
  };

  const handleSubmitSearch = (value) => {
    setSearchKeywords(value);
    setCurrentPage(1);
  };

  const handleChangeCurrentPage = (current) => {
    setCurrentPage(current);
  };

  return (
    <div className="manage-request-block">
      <div className="manage-request-block__title">Request List</div>
      <div className="manage-request-block__action">
        <div style={{ display: 'flex' }}>
          <FilterMenu
            id="manage-request-block__filter"
            options={LIST_STATUS}
            value={currentChoosedStatus}
            onChange={handleChangeFilterMenu}
            title="State"
            checkboxType="ASSET_STATE"
          />
          <DatePicker
            id="manage-request-block__date-picker"
            placeholder="Returned Date"
            format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
            onChange={handleValidateDateOfBirth}
            style={{ marginLeft: '20px' }}
          />
        </div>
        <Search
          allowClear
          className="search-input"
          onSearch={handleSubmitSearch}
          enterButton
          id="manager-request-block__search"
        />
      </div>
      <div className="manage-request-block__table">
        {isLoading ? (
          <Space size="middle">
            <Spin size="large" style={{ paddingLeft: '30rem', paddingTop: '10rem' }} />
          </Space>
        ) : (
          <TableRequest
            id="manage-request-block__table"
            currentPage={currentPage}
            listAssets={listAssets}
            totalRow={totalRow}
            pageSize={PAGE_SIZE}
            handleChangeCurrentPage={handleChangeCurrentPage}
            searchKeywords={searchKeywords}
            statuses={convertListStatus(currentChoosedStatus)}
            date={date}
            fetchListRequest={fetchListRequest}
          />
        )}
      </div>
    </div>
  );
};

export default ManageRequestReturn;
