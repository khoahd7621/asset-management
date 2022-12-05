import { Col, Row, Table } from 'antd';

import CustomModal from '../Modal/Modal';

import './TableMyAssignment.scss';

const ModalAssignAssetDetail = ({ open, onCancel, data }) => {
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
      className="modal-asign-asset-detail"
      title="Detail Asset Information"
      open={open}
      onCancel={onCancel}
      width="450px"
    >
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Asset Code</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.assetCode ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Asset Name</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.assetName?.replaceAll(' ', '\u00a0') ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Specification</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.specification ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign To</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.userAssignedTo ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign By</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.userAssignedBy ?? 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign Date</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.assignedDate ? convertStrDate(data.assignedDate) : 'Loading...'}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">State</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">
            {data.status ? capitalizeFirstLetter(data.status.toLowerCase().replaceAll('_', ' ')) : 'Loading...'}
          </div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Note</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.note ?? 'Loading...'}</div>
        </Col>
      </Row>
    </CustomModal>
  );
};

export default ModalAssignAssetDetail;
