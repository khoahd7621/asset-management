import { useEffect, useState } from 'react';
import { Button, Input, Modal, Table } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';

import './Modal.scss';

import { getAllUsers } from '../../../services/getApiService';

const ModalChooseUser = ({ open, onCancel, currentUser, handleSaveChoose }) => {
  const { Search } = Input;
  const TableColumns = [
    {
      title: (
        <span>
          Staff Code <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'staffCode',
      key: 'staffCode',
      ellipsis: true,
      sorter: (a, b) => a.staffCode.localeCompare(b.staffCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '150px',
    },
    {
      title: (
        <span>
          Full Name <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'fullName',
      key: 'fullName',
      ellipsis: true,
      sorter: (a, b) => a.fullName.localeCompare(b.fullName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Type <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      sorter: (a, b) => a.type.localeCompare(b.type),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '130px',
    },
  ];

  const [datas, setDatas] = useState([]);
  const [selectedKey, setSelectedKey] = useState([currentUser.userId]);
  const [currentData, setCurrentData] = useState({
    ...currentUser,
  });
  const [dataSource, setDataSource] = useState([...datas]);

  useEffect(() => {
    fetchListUsers();
  }, []);

  const fetchListUsers = async () => {
    const response = await getAllUsers();
    if (response && response.status === 200) {
      setDatas(
        response?.data.map((item, index) => {
          return {
            key: item.id,
            staffCode: item.staffCode,
            fullName: item.fullName,
            type: item.type,
          };
        }),
      );
      setDataSource(
        response?.data.map((item, index) => {
          return {
            key: item.id,
            staffCode: item.staffCode,
            fullName: item.fullName,
            type: item.type,
          };
        }),
      );
    }
  };

  const handleOnSearch = (keyword) => {
    setDataSource(
      datas.filter(
        (user) =>
          user?.staffCode?.toLowerCase()?.includes(keyword.toLowerCase()) ||
          user?.fullName?.toLowerCase()?.includes(keyword.toLowerCase()),
      ),
    );
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
          onRow={(record, rowIndex) => ({
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
      <div className="modal-choose__action">
        <Button className="save" type="primary" htmlType="submit" disabled={!currentData.userId} onClick={handleSave}>
          Save
        </Button>
        <Button htmlType="button" onClick={() => onCancel()}>
          Cancel
        </Button>
      </div>
    </Modal>
  );
};

export default ModalChooseUser;
