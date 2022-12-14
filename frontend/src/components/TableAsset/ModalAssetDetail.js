import { Col, Row, Table } from 'antd';
import dateConverter from '../../utils/convertDateUtil';
import convertEnum from '../../utils/convertEnumUtil';

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
          <div className="content">
            {data.installedDate ? dateConverter.convertStrDate(data.installedDate) : 'Loading...'}
          </div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={4}>
          <div className="title">State</div>
        </Col>
        <Col span={16} sm={19} md={20}>
          <div className="content">
            {data.status ? convertEnum.toShow(data.status) : 'Loading...'}
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
