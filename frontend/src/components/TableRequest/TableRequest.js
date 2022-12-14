import { Table } from 'antd';
import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons';
import { useState } from 'react';

import './TableRequest.scss';

import Pagination from '../Pagination/Pagination';
import { CheckIcon, DeclineIcon } from '../../assets/CustomIcon';
import ModalRequestCompleted from './ModalRequestCompleted';
import ModalRequestCanceled from './ModalRequestCanceled';
import convertDate from '../../utils/convertDateUtil';

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

  const TableAssetColumns = [
    {
      title: title('No.', 'no'),
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
      title: title('Asset Code', 'assetCode'),
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
      title: title('Asset Name', 'assetName'),
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
      title: title('Requested By', 'requestBy'),
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
      title: title('Assigned Date', 'date'),
      dataIndex: 'date',
      key: 'date',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{convertDate.convertStrDate(text)}</div>;
      },
      sorter: (a, b) => a.date.localeCompare(b.date),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Accepted By', 'acceptedBy'),
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
      title: title('Returned Date', 'returnDate'),
      dataIndex: 'returnDate',
      key: 'returnDate',
      ellipsis: true,
      render: (text, _record) => {
        return <div className="col-btn">{convertDate.convertStrDate(text)}</div>;
      },
      sorter: (a, b) => checkString(a.returnDate).localeCompare(checkString(b.returnDate)),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('State', 'state'),
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
        onChange={onChangeSortOrder}
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
