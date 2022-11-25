import { Table } from 'antd';
import { CaretDownOutlined, CheckOutlined, UndoOutlined, CloseOutlined } from '@ant-design/icons';

import './Home.scss';

import { PasswordModal } from '../../../components';

const Home = () => {
  const tableColumns = [
    {
      title: (
        <span>
          Asset Code <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'assetCode',
      key: 'assetCode',
      ellipsis: true,
      render: (text, record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.assetCode > b.assetCode,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Asset Name <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'assetName',
      key: 'assetName',
      ellipsis: true,
      render: (text, record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.assetName > b.assetName,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Category <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'category',
      key: 'category',
      ellipsis: true,
      render: (text, record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.category > b.category,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          AssignedDate <CaretDownOutlined />
        </span>
      ),
      key: 'assignedDate',
      dataIndex: 'assignedDate',
      ellipsis: true,
      render: (text, record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.assignedDate > b.assignedDate,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          State <CaretDownOutlined />
        </span>
      ),
      key: 'state',
      dataIndex: 'state',
      ellipsis: true,
      render: (text, record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.state > b.state,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      key: 'action',
      width: '100px',
      render: (text, record) => {
        return (
          <div className="col-action in-active">
            <button className="check-btn" disabled={record.state === 'Accepted'}>
              <CheckOutlined />
            </button>
            <button className="delete-btn" disabled={record.state === 'Accepted'}>
              <CloseOutlined />
            </button>
            <button className="return-btn" disabled={record.state === 'Waiting for acceptance'}>
              <UndoOutlined />
            </button>
          </div>
        );
      },
    },
  ];

  const tableData = [
    {
      key: 'PC100125',
      assetCode: 'PC100125',
      assetName: 'Personal Computer',
      category: 'Personal Computer',
      assignedDate: '07/06/2020',
      state: 'Accepted',
    },
  ];

  return (
    <div className="staff-home-block">
      <div className="staff-home-block__title">My Assignment</div>
      <div className="staff-home-block__table">
        <Table
          className="table-assignment"
          showSorterTooltip={false}
          rowSelection={false}
          columns={tableColumns}
          dataSource={tableData}
          pagination={false}
        />
      </div>
      <PasswordModal />
    </div>
  );
};

export default Home;
