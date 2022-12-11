import { Table } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';
import { useState } from 'react';

import './TableRequest.scss';

import Pagination from '../Pagination/Pagination';
import { CheckIcon, DeclineIcon } from '../../assets/CustomIcon';
import ModalRequestCompleted from './ModalRequestCompleted';
import ModalRequestCanceled from './ModalRequestCanceled';

const TableRequest = ({
  listAssets = [],
  currentPage = 1,
  totalRow = 1,
  pageSize = 20,
  handleChangeCurrentPage,
  fetchListRequest,
  searchKeywords,
  statuses,
  date,
}) => {
  const [isComplete, setIsComplete] = useState();
  const [isCancel, setIsCancel] = useState();
  const [returnAssetId, setReturnAssetId] = useState();

  const onClickToCompleteRequest = (requestId) => {
    setReturnAssetId(requestId);
    setIsComplete(true);
  };

  const onClickToCancelRequest = (requestId) => {
    setReturnAssetId(requestId);
    setIsCancel(true);
  };

  const TableAssetColumns = [
    {
      title: (
        <span>
          No. <CaretDownOutlined />
        </span>
      ),
      width: '4.5em',
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
      dataIndex: 'acceptedBy',
      key: 'acceptedBy',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{text}</div>;
      },
      sorter: (a, b) => checkString(a.acceptedBy).localeCompare(checkString(b.acceptedBy)),
      sortDirections: ['ascend', 'descend', 'ascend'],
      responsive: ['sm'],
    },
    {
      title: (
        <span>
          Returned Date <CaretDownOutlined />
        </span>
      ),
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
      width: '60px',
      render: (_text, record) => {
        return (
          <div className="col-action in-active">
            <button
              onClick={() => onClickToCompleteRequest(record.assetId)}
              className="check-icon"
              disabled={record.state === 'Completed'}
            >
              <CheckIcon />
            </button>

            <button
              onClick={() => onClickToCancelRequest(record.assetId)}
              className="decline-icon"
              disabled={record.state === 'Completed'}
            >
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

      <ModalRequestCompleted
        open={isComplete}
        onCancel={() => {
          setIsComplete(false);
          setReturnAssetId(0);
        }}
        data={returnAssetId}
        searchKeywords={searchKeywords}
        statuses={statuses}
        date={date}
        fetchListRequest={fetchListRequest}
        handleChangePage={handleChangeCurrentPage}
      />

      <ModalRequestCanceled
        open={isCancel}
        onCancel={() => {
          setIsCancel(false);
          setReturnAssetId(0);
        }}
        data={returnAssetId}
        searchKeywords={searchKeywords}
        statuses={statuses}
        date={date}
        fetchListRequest={fetchListRequest}
        handleChangePage={handleChangeCurrentPage}
      />
    </>
  );
};

export default TableRequest;
