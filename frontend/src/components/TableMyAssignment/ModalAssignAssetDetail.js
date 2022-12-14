import { Col, Row } from 'antd';
import convertDate from '../../utils/convertDateUtil';
import convertEnum from '../../utils/convertEnumUtil';

import CustomModal from '../Modal/Modal';

import './TableMyAssignment.scss';

const ModalAssignAssetDetail = ({ open, onCancel, data }) => {
  return (
    <CustomModal
      className="modal-asign-asset-detail"
      title="Detail Asset Information"
      open={open}
      onCancel={onCancel}
      width="35rem"
    >
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Asset Code</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.assetCode ?? ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Asset Name</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.assetName?.replaceAll(' ', '\u00a0') ?? ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Specification</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.specification ?? ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign To</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.userAssignedTo ?? ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign By</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data?.userAssignedBy ?? ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Assign Date</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.assignedDate ? convertDate.convertStrDate(data.assignedDate) : ''}</div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">State</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">
            {data.status === 'ACCEPTED' && data.returnAsset !== null
              ? 'Waiting for returning'
              : convertEnum.toShow(data.status)}
          </div>
        </Col>
      </Row>
      <Row>
        <Col span={8} sm={5} md={8}>
          <div className="title">Note</div>
        </Col>
        <Col span={16} sm={19} md={16}>
          <div className="content">{data.note ?? ''}</div>
        </Col>
      </Row>
    </CustomModal>
  );
};

export default ModalAssignAssetDetail;
