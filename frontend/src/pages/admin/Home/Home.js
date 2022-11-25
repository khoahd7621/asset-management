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
      key: 'LA100002',
      assetCode: 'LA100002',
      assetName: 'Laptop HP Probook 450 G1',
      category: 'Laptop',
      assignedDate: '10/04/2019',
      state: 'Accepted',
    },
    {
      key: 'LA100003',
      assetCode: 'LA100003',
      assetName: 'Laptop HP Probook 450 G1',
      category: 'Laptop',
      assignedDate: '10/04/2019',
      state: 'Accepted',
    },
    {
      key: 'MO100004',
      assetCode: 'MO100004',
      assetName: 'Monitor Dell UltraSharp',
      category: 'Monitor',
      assignedDate: '20/03/2021',
      state: 'Waiting for acceptance',
    },
  ];

  return (
    <div className="admin-home-block">
      <div className="admin-home-block__title">My Assignment</div>
      <div className="admin-home-block__table">
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
