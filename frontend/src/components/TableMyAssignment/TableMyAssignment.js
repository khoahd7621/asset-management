import { useEffect, useState } from 'react';
import { Modal, Table } from 'antd';
import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons';

import './TableMyAssignment.scss';

import ModalAssignAssetDetail from './ModalAssignAssetDetail';
import { getAllMyAssignAsset, getAssignAssetDetails } from '../../services/getApiService';
import { putAcceptAssignAsset, putDeclineAssignAsset } from '../../services/editApiService';
import FirstPasswordModal from '../FirstPasswordModal/FirstPasswordModal';
import { postCreateNewRequestReturn } from '../../services/createApiService';
import { CheckIcon, DeclineIcon, RefreshIcon } from '../../assets/CustomIcon';
import convertDate from '../../utils/convertDateUtil';
import convertEnum from '../../utils/convertEnumUtil';

const TableMyAssignment = () => {
  const [isShowModalAssignAssetDetail, setIsShowModalAssignAssetDetail] = useState(false);
  const [data, setData] = useState([]);
  const [modalData, setModalData] = useState({});
  const [confirmPopUp, setConfirmPopUp] = useState(false);
  const [declinePopUp, setDeclinePopUp] = useState(false);
  const [acceptPopUp, setAcceptPopUp] = useState(false);
  const [idAccept, setIdAccept] = useState();
  const [idDecline, setIdDecline] = useState();
  const [idRequest, setIdRequest] = useState();

  useEffect(() => {
    fetchGetMyAssignAsset();
  }, []);

  const fetchGetMyAssignAsset = async () => {
    const response = await getAllMyAssignAsset();
    if (response && response.status === 200) {
      setData(
        response?.data?.map((item) => {
          return {
            key: item.id,
            assignAssetId: item.id,
            assetCode: item.assetCode,
            assetName: item.assetName.replaceAll(' ', '\u00a0'),
            category: item.category,
            assignedDate: convertDate.convertStrDate(item.assignedDate),
            state: convertEnum.toShow(item.status),
            request: item.returnAsset,
          };
        }),
      );
    } else {
      setData([]);
    }
  };

  const onClickToReturn = async () => {
    const response = await postCreateNewRequestReturn({ idRequest });
    if (response && response.status === 200) {
      fetchGetMyAssignAsset();
    }
    setConfirmPopUp(false);
  };

  const onclickShowReturn = (idAssign) => {
    setIdRequest(idAssign);
    setConfirmPopUp(true);
  };

  const onClickToAccept = async () => {
    const response = await putAcceptAssignAsset({ idAccept });
    if (response && response.status === 200) {
      fetchGetMyAssignAsset();
    }
    setAcceptPopUp(false);
  };

  const onclickShowAccept = (id) => {
    setIdAccept(id);
    setAcceptPopUp(true);
  };

  const onClickToDecline = async () => {
    const response = await putDeclineAssignAsset({ idDecline });
    if (response && response.status === 200) {
      fetchGetMyAssignAsset();
    }
    setDeclinePopUp(false);
  };

  const onclickShowDecline = (id) => {
    setIdDecline(id);
    setDeclinePopUp(true);
  };

  const onclickShowDetail = () => {
    setIsShowModalAssignAssetDetail(true);
  };

  const handleClickRecord = async (assignAssetId) => {
    const response = await getAssignAssetDetails(assignAssetId);
    if (response && response.status === 200) {
      setModalData(response.data);
    }
    onclickShowDetail();
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

  const tableColumns = [
    {
      title: title('Asset Code', 'assetCode'),
      dataIndex: 'assetCode',
      key: 'assetCode',
      ellipsis: true,
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assignAssetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Asset Name', 'assetName'),
      dataIndex: 'assetName',
      key: 'assetName',
      ellipsis: true,
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assignAssetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Category', 'category'),
      dataIndex: 'category',
      key: 'category',
      ellipsis: true,
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assignAssetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) => a.category.localeCompare(b.category),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Assigned Date', 'assignedDate'),
      key: 'assignedDate',
      dataIndex: 'assignedDate',
      ellipsis: true,
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assignAssetId)}>
            {text}
          </div>
        );
      },
      sorter: (a, b) =>
        convertDate.formatDate(convertDate.convertStrDate(a.assignedDate)) -
        convertDate.formatDate(convertDate.convertStrDate(b.assignedDate)),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('State', 'state'),
      key: 'state',
      dataIndex: 'state',
      ellipsis: true,
      render: (text, record) => {
        return (
          <div className="col-btn" onClick={() => handleClickRecord(record.assignAssetId)}>
            {record.state === 'Accepted' && record.request !== null ? 'Waiting for returning' : text}
          </div>
        );
      },
      sorter: (a, b) => a.state.localeCompare(b.state),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      key: 'action',
      width: '100px',
      render: (_text, record) => {
        return (
          <div className="col-action in-active">
            <button
              className="check-icon"
              disabled={record.state === 'Accepted'}
              onClick={() => onclickShowAccept(record.assignAssetId)}
            >
              <CheckIcon />
            </button>
            <button
              className="decline-icon"
              disabled={record.state === 'Accepted'}
              onClick={() => onclickShowDecline(record.assignAssetId)}
            >
              <DeclineIcon />
            </button>
            <button
              className="return-icon"
              disabled={record.state === 'Accepted' && record.request === null ? false : true}
              onClick={() => onclickShowReturn(record.assignAssetId)}
            >
              <RefreshIcon />
            </button>
          </div>
        );
      },
    },
  ];

  return (
    <>
      <div className="home-block__table">
        <Table
          id="table-mt-assignment"
          className="table-assignment"
          showSorterTooltip={false}
          rowSelection={false}
          columns={tableColumns}
          dataSource={data}
          pagination={false}
          onChange={onChangeSortOrder}
        />
      </div>
      <FirstPasswordModal />
      <Modal
        open={confirmPopUp}
        className="user-list__disable-modal"
        title={'Are you sure?'}
        centered
        onOk={onClickToReturn}
        onCancel={() => setConfirmPopUp(false)}
        okText="Yes"
        cancelText="No"
        closable={false}
      >
        <p>Do you want to create a returning request for this asset?</p>
      </Modal>
      <Modal
        open={acceptPopUp}
        className="user-list__disable-modal"
        title={'Are you sure?'}
        centered
        onOk={onClickToAccept}
        onCancel={() => setAcceptPopUp(false)}
        okText="Accept"
        cancelText="Cancel"
        closable={false}
      >
        <p>Do you want to accept this assignment?</p>
      </Modal>
      <Modal
        open={declinePopUp}
        className="user-list__disable-modal"
        title={'Are you sure?'}
        centered
        onOk={onClickToDecline}
        onCancel={() => setDeclinePopUp(false)}
        okText="Decline"
        cancelText="Cancel"
        closable={false}
      >
        <p>Do you want to decline this assignment?</p>
      </Modal>
      <ModalAssignAssetDetail
        open={isShowModalAssignAssetDetail}
        onCancel={() => {
          setIsShowModalAssignAssetDetail(false);
          setModalData({});
        }}
        data={modalData}
      />
    </>
  );
};
export default TableMyAssignment;
