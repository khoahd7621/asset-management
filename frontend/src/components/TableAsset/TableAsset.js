import { useState } from 'react';
import { Table, Pagination, Modal, Row, Col } from 'antd';
import { CaretDownOutlined, CloseSquareOutlined } from '@ant-design/icons';

import './TableAsset.scss';

import { DeleteIcon, EditIcon } from '../../assets/CustomIcon';
import { getAssetDetailAndItsHistories } from '../../services/getApiService';

const TableAsset = ({
  listAssets = [],
  currentPage = 1,
  totalRow = 1,
  pageSize = 20,
  handleChangeCurrentPage,
  handleChangeSizePage,
}) => {
  const TableAssetColumns = [
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
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assetId)}>
            {text}
          </div>
        );
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
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assetId)}>
            {text}
          </div>
        );
      },
      defaultSortOrder: 'ascend',
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
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
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) => a.category.localeCompare(b.category),
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
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) => a.state.localeCompare(b.state),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      key: 'action',
      width: '100px',
      render: (text, record) => {
        return (
          <div className="col-action in-active">
            <button className="edit-btn" disabled={record.state === 'Assigned'}>
              <EditIcon />
            </button>
            <button className="delete-btn" disabled={record.state === 'Assigned'}>
              <DeleteIcon />
            </button>
          </div>
        );
      },
    },
  ];

  const TableHistoryColumns = [
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
      ellipsis: true,
    },
    {
      title: 'Assigned to',
      dataIndex: 'assignedTo',
      key: 'assignedTo',
      ellipsis: true,
    },
    {
      title: 'Assigned by',
      dataIndex: 'assignedBy',
      key: 'assignedBy',
      ellipsis: true,
    },
    {
      title: 'Returned Date',
      dataIndex: 'returnedDate',
      key: 'returnedDate',
      ellipsis: true,
    },
  ];

  const [isShowModalAssetDetail, setIsShowModalAssetDetail] = useState(false);
  const [modalData, setModalData] = useState({});
  const [listHistory, setListHistory] = useState([]);

  const handleClickRecord = async (assetId) => {
    const response = await getAssetDetailAndItsHistories(assetId);
    if (response && response.status === 200) {
      setModalData(response?.data.asset);
      setListHistory(
        response?.data?.histories.length === 0
          ? []
          : response?.data?.histories.map((item, index) => {
              return {
                key: index,
                date: convertStrDate(item.assignedDate),
                assignedTo: item.assignedTo,
                assignedBy: item.assignedBy,
                returnedDate: convertStrDate(item.returnedDate),
              };
            }),
      );

      setIsShowModalAssetDetail(true);
    }
  };

  const handleChangePage = (current) => {
    handleChangeCurrentPage(current);
  };

  const handleChangePageSize = (current, pageSize) => {
    handleChangeSizePage(current, pageSize);
  };

  const capitalizeFirstLetter = (string) => string.charAt(0).toUpperCase() + string.slice(1);

  const convertStrDate = (dateStr) => {
    const date = new Date(dateStr);
    return (
      (date.getDate() > 9 ? date.getDate() : '0' + date.getDate()) +
      '/' +
      (date.getMonth() > 8 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1)) +
      '/' +
      date.getFullYear()
    );
  };

  return (
    <>
      <Table
        className="table-asset"
        showSorterTooltip={false}
        rowSelection={false}
        columns={TableAssetColumns}
        dataSource={listAssets}
        pagination={false}
      />
      <Pagination
        className="table-asset-pagination"
        showSizeChanger
        onShowSizeChange={handleChangePageSize}
        onChange={handleChangePage}
        current={currentPage}
        defaultPageSize={pageSize}
        total={totalRow}
        itemRender={itemRender}
      />
      <Modal
        className="modal-asset-detail"
        title="Detail Asset Information"
        centered
        open={isShowModalAssetDetail}
        onCancel={() => {
          setIsShowModalAssetDetail(false);
          setModalData({});
          setListHistory([]);
        }}
        closeIcon={<CloseSquareOutlined />}
        footer={null}
        width="700px"
      >
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Asset Code</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">{modalData.assetCode ?? 'Loading...'}</div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Asset Name</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">{modalData.assetName ?? 'Loading...'}</div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Category</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">{modalData?.category?.name ?? 'Loading...'}</div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Installed Date</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">
              {modalData.installedDate ? convertStrDate(modalData.installedDate) : 'Loading...'}
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">State</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">
              {modalData.status
                ? capitalizeFirstLetter(modalData.status.toLowerCase().replaceAll('_', ' '))
                : 'Loading...'}
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Location</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">{modalData.location ?? 'Loading...'}</div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">Specification</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="content">{modalData.specification ?? 'Loading...'}</div>
          </Col>
        </Row>
        <Row>
          <Col span={8} sm={5} md={4}>
            <div className="title">History</div>
          </Col>
          <Col span={16} sm={19} md={20}>
            <div className="table-history-wrapper">
              <Table
                className="table-history-assign"
                showSorterTooltip={false}
                rowSelection={false}
                columns={TableHistoryColumns}
                dataSource={listHistory}
                pagination={false}
                size={'small'}
              />
            </div>
          </Col>
        </Row>
      </Modal>
    </>
  );
};

const itemRender = (_, type, originalElement) => {
  if (type === 'prev') {
    return <a>Previous</a>;
  }
  if (type === 'next') {
    return <a>Next</a>;
  }
  return originalElement;
};

export default TableAsset;
