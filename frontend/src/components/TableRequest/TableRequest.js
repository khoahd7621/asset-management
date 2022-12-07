import { Table } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';

import './TableRequest.scss';

import Pagination from '../Pagination/Pagination';
import { adminRoute } from '../../routes/routes';
import { CheckIcon, DeclineIcon } from '../../assets/CustomIcon';

const TableRequest = ({ listAssets = [], currentPage = 1, totalRow = 1, pageSize = 20, handleChangeCurrentPage }) => {
  const TableAssetColumns = [
    {
      title: (
        <span>
          No <CaretDownOutlined />
        </span>
      ),
      width: '3.5rem',
      dataIndex: 'no',
      key: 'no',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.no - b.no,
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Asset Code <CaretDownOutlined />
        </span>
      ),
      width: '104px',
      dataIndex: 'assetCode',
      key: 'assetCode',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Asset Name <CaretDownOutlined />
        </span>
      ),
      width: '10rem',
      dataIndex: 'assetName',
      key: 'assetName',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Requested By <CaretDownOutlined />
        </span>
      ),
      width: '115px',
      dataIndex: 'requestBy',
      key: 'requestBy',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.requestBy.localeCompare(b.requestBy),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Assigned Date <CaretDownOutlined />
        </span>
      ),
      width: '124px',
      dataIndex: 'date',
      key: 'date',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{convertStrDate(text)}</div>;
      },
      sorter: (a, b) => a.date.localeCompare(b.date),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Accepted By <CaretDownOutlined />
        </span>
      ),
      width: '102px',
      dataIndex: 'acceptedBy',
      key: 'acceptedBy',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => checkString(a.acceptedBy).localeCompare(checkString(b.acceptedBy)),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Returned Date <CaretDownOutlined />
        </span>
      ),
      width: '120px',
      dataIndex: 'returnDate',
      key: 'returnDate',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{convertStrDate(text)}</div>;
      },
      sorter: (a, b) => checkString(a.returnDate).localeCompare(checkString(b.returnDate)),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          State <CaretDownOutlined />
        </span>
      ),
      width: '135px',
      key: 'state',
      dataIndex: 'state',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => a.state.localeCompare(b.state),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      key: 'action',
      width: '5rem',
      render: (_text, record) => {
        return (
          <div className="col-action in-active">
            <button className="check-icon" disabled={record.state === 'Completed'}>
              <CheckIcon />
            </button>

            <button className="decline-icon" disabled={record.state === 'Completed'}>
              <DeclineIcon />
            </button>
          </div>
        );
      },
    },
  ];

  const handleChangePage = (current) => {
    handleChangeCurrentPage(current);
  };

  const convertStrDate = (dateStr) => {
    if (dateStr === null) return '';
    const date = new Date(dateStr);
    return (
      (date.getDate() > 9 ? date.getDate() : '0' + date.getDate()) +
      '/' +
      (date.getMonth() > 8 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1)) +
      '/' +
      date.getFullYear()
    );
  };

  const checkString = (string) => {
    if (string === null) {
      return '';
    } else {
      return string;
    }
  };

  return (
    <>
      <Table
        id="table-request"
        className="table-request"
        showSorterTooltip={false}
        rowSelection={false}
        columns={TableAssetColumns}
        dataSource={listAssets}
        pagination={false}
      />
      <Pagination onChange={handleChangePage} current={currentPage} defaultPageSize={pageSize} total={totalRow} />
    </>
  );
};

export default TableRequest;
