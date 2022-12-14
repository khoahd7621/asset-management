import { useEffect, useState } from 'react';
import { Button, Input, Modal, Table } from 'antd';
import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons';

import './Modal.scss';

import { searchUsersWithKeywordAndTypesWithPagination } from '../../services/findApiService';
import CustomPagination from '../Pagination/Pagination';
import convertEnum from '../../utils/convertEnumUtil';

const ModalChooseUser = ({ open, onCancel, currentUser, handleSaveChoose }) => {
  const { Search } = Input;
  const [field, setField] = useState();
  const [order, setOrder] = useState();

  function onChangeSortOrder(_pagination, _filters, sorter, _extra) {
    setField(sorter.field);
    setOrder(sorter.order);
  }

  const title = (title, dataIndex) => {
    return (
      <span>
        {title} {order === 'ascend' && field === dataIndex ? <CaretUpOutlined /> : <CaretDownOutlined />}
      </span>
    );
  };
  const TableColumns = [
    {
      title: title('Staff Code', 'staffCode'),
      dataIndex: 'staffCode',
      key: 'staffCode',
      ellipsis: true,
      sorter: (a, b) => a.staffCode.localeCompare(b.staffCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '150px',
    },
    {
      title: title('Full Name', 'fullName'),
      dataIndex: 'fullName',
      key: 'fullName',
      ellipsis: true,
      sorter: (a, b) => a.fullName.localeCompare(b.fullName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Type', 'type'),
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      sorter: (a, b) => a.type.localeCompare(b.type),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '130px',
      render: (text) => {
        return (convertEnum.toShow(text));
      },
    },
  ];
  const PAGE_SIZE = 10;

  const [totalRow, setTotalRow] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [datas, setDatas] = useState([]);
  const [selectedKey, setSelectedKey] = useState([currentUser.userId]);
  const [currentData, setCurrentData] = useState({
    ...currentUser,
  });
  const [dataSource, setDataSource] = useState([...datas]);

  useEffect(() => {
    fetchListUsers('', '', PAGE_SIZE, currentPage - 1, '', '');
  }, []);

  const fetchListUsers = async (keyWord, types, limit, page, sortField, sortType) => {
    const response = await searchUsersWithKeywordAndTypesWithPagination({
      keyWord,
      types,
      limit,
      page,
      sortField,
      sortType,
    });
    if (response && response.status === 200) {
      setDatas(
        response?.data?.data.map((item, _index) => {
          return {
            key: item.id,
            staffCode: item.staffCode,
            fullName: item.fullName,
            type: item.type,
          };
        }),
      );
      setDataSource(
        response?.data?.data.map((item, _index) => {
          return {
            key: item.id,
            staffCode: item.staffCode,
            fullName: item.fullName,
            type: item.type,
          };
        }),
      );
      setTotalRow(response?.data?.totalRow);
    }
  };

  const handleChangePage = (current) => {
    setCurrentPage(current);
    fetchListUsers(searchKeywords, '', PAGE_SIZE, current - 1, '', '');
  };

  const handleOnSearch = (keyword) => {
    fetchListUsers(keyword, '', PAGE_SIZE, 0, '', '');
    setCurrentPage(1);
    setSearchKeywords(keyword);
  };

  const handleSave = () => {
    handleSaveChoose('USER', currentData);
  };

  return (
    <Modal className="modal-choose" open={open} closable={false} footer={false} width={'700px'} mask={false}>
      <div className="modal-choose__header">
        <div className="title">Select User</div>
        <Search
          id="choose-user-search-input"
          allowClear
          className="search-input"
          onSearch={(value) => handleOnSearch(value)}
          enterButton
        />
      </div>
      <div className="modal-choose__table">
        <Table
          id="chooose-user-table"
          size="small"
          rowSelection={{
            type: 'radio',
            selectedRowKeys: selectedKey,
            onChange: (selectedRowKeys, selectedRows) => {
              setCurrentData({
                userId: selectedRowKeys[0],
                fullName: selectedRows[0].fullName,
              });
              setSelectedKey(selectedRowKeys);
            },
          }}
          showSorterTooltip={false}
          columns={TableColumns}
          dataSource={dataSource}
          pagination={false}
          onChange={onChangeSortOrder}
          onRow={(record, _rowIndex) => ({
            onClick: () => {
              setCurrentData({
                userId: record.key,
                fullName: record.fullName,
              });
              setSelectedKey([record.key]);
            },
          })}
        />
      </div>
      <div className="modal-choose__pagination">
        <CustomPagination
          onChange={handleChangePage}
          current={currentPage}
          defaultPageSize={PAGE_SIZE}
          total={totalRow}
        />
      </div>
      <div className="modal-choose__action">
        <Button className="save" type="primary" htmlType="submit" disabled={!currentData.userId} onClick={handleSave}>
          Save
        </Button>
        <Button
          htmlType="button"
          onClick={() => {
            setSelectedKey([currentUser.userId]);
            setCurrentData({ ...currentUser });
            onCancel();
          }}
        >
          Cancel
        </Button>
      </div>
    </Modal>
  );
};

export default ModalChooseUser;
