import { Col, Row, Table } from 'antd';

import CustomModal from '../Modal/Modal';

const ModalAssetDetail = ({ open, onCancel, data, listHistories }) => {
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
    <CustomModal
      className="modal-asset-detail"
      title="Detail Asset Information"
      open={open}
      onCancel={onCancel}
      width="700px"
    >
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Asset Code</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data.assetCode ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Asset Name</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data?.assetName?.replaceAll(' ', '\u00a0') ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Category</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data?.category?.name ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Installed Date</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data.installedDate ? convertStrDate(data.installedDate) : 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">State</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">
            {data.status ? capitalizeFirstLetter(data.status.toLowerCase().replaceAll('_', ' ')) : 'Loading...'}
          </div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Location</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data.location ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">Specification</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">{data.specification ?? 'Loading...'}</div>
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
              dataSource={listHistories}
              pagination={false}
              size={'small'}
            />
          </div>
        </Col>
      </Row>
    </CustomModal>
  );
};

export default ModalAssetDetail;
