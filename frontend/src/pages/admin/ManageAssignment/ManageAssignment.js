import React, { useEffect, useState } from 'react';
import { Row, Col, Table, Modal, Button, Popover, Checkbox, DatePicker, Input, Space, Spin } from 'antd';
import { useLocation, useNavigate } from 'react-router-dom';
import { CloseCircleOutlined, CaretUpOutlined, CaretDownOutlined } from '@ant-design/icons';

import './ManageAssignment.scss';

import { filterAssignmentList } from '../../../services/findApiService';
import { CloseIcon, EditIcon, FilterIcon, RefreshIcon } from '../../../assets/CustomIcon';
import CustomPagination from '../../../components/Pagination/Pagination';
import { getAssignmentDetails } from '../../../services/getApiService';
import { adminRoute } from '../../../routes/routes';
import { deleteAssignment } from '../../../services/disableApiService';
import { postCreateNewRequestReturn } from '../../../services/createApiService';
import convertDate from '../../../utils/convertDateUtil';
import convertEnum from '../../../utils/convertEnumUtil';

const ManageAssignment = () => {
  const { Search } = Input;
  const location = useLocation();
  const navigate = useNavigate();
  const [assignmentList, setAssignmentList] = useState([]);
  const [assignmentDetails, setAssignmentDetails] = useState();
  const [assignmentId, setAssignmentId] = useState();
  const [idRequest, setIdRequest] = useState();
  const [isModalAssignmentDetails, SetIsModalAssignmentDetails] = useState(false);
  const [confirmPopUp, setConfirmPopUp] = useState(false);
  const [requestReturnPopUp, setRequestReturnPopUp] = useState(false);
  const [isDelete, setIsDelete] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const plainOptions = ['Accepted', 'Waiting for acceptance', 'Declined'];
  const defaultCheckedList = ['ALL'];
  const [checkedList, setCheckedList] = useState();
  const [checkAll, setCheckAll] = useState(true);

  const [totalRow, setTotalRow] = useState();
  const [current, setCurrent] = useState(1);

  //filter data
  const [getSearchData, setGetSearchData] = useState('');
  const [assigndate, setAssigndate] = useState('');
  const [state, setState] = useState(plainOptions);

  useEffect(() => {
    document.title = 'Manage Assignment - Assignment List';
  }, []);

  useEffect(() => {
    setIsLoading(true);
    getData();
  }, [current, assigndate, getSearchData, state, isDelete]);

  const getData = async () => {
    const response = await filterAssignmentList(getSearchData, state.map(convertEnum.toGet), assigndate, current - 1);
    if (response.status === 200) {
      setIsLoading(false);
      const assignmentResponse = location.state?.assignmentResponse;
      if (assignmentResponse && isDelete === false) {
        const listDatas = response?.data?.data.filter((item) => item.id !== assignmentResponse.id);
        listDatas.unshift(assignmentResponse);
        setAssignmentList(
          listDatas.map((item, index) => {
            return { ...item, no: index + 1, key: item.id };
          }),
        );
      } else {
        setAssignmentList(
          response?.data?.data.map((item, index) => {
            return { ...item, no: index + 1, key: item.id };
          }),
        );
      }
      setTotalRow(response.data.totalRow);
      window.history.replaceState({}, document.title);
    }
  };

  const showAssignmentDetailsModal = async (assignmentId) => {
    const response = await getAssignmentDetails(assignmentId.currentTarget.dataset.id);
    if (response.status === 200) {
      setIsLoading(false);
      setAssignmentDetails(response.data);
      SetIsModalAssignmentDetails(true);
    }
  };

  const ModalAssignmentDetails = () => {
    setAssignmentDetails();
    SetIsModalAssignmentDetails(false);
  };

  const onClickToEdit = (assignmentId) => {
    navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}/${adminRoute.editAssignment}/${assignmentId}`);
  };

  const onClickToDelete = async () => {
    const response = await deleteAssignment(assignmentId);
    if (response.status === 204) {
      setIsDelete(true);
      setConfirmPopUp(false);
      getData();
    }
  };

  const onClickToAbleDelete = (data) => {
    setAssignmentId(data.currentTarget.dataset.id);
    setConfirmPopUp(true);
  };

  const onClickToAbleRequestReturn = (assignmentId) => {
    setIdRequest(assignmentId);
    setRequestReturnPopUp(true);
  };

  const onClickToRequestReturn = async () => {
    const response = await postCreateNewRequestReturn({ idRequest });
    if (response.status === 200) {
      getData();
      setRequestReturnPopUp(false);
    }
  };

  const CheckboxGroup = Checkbox.Group;

  const onChange = async (list) => {
    setCurrent(1);
    setCheckedList(list);
    setState(list);
    setCheckAll(list.length === plainOptions.length);
    if (list.length === plainOptions.length) {
      setCheckAll(true);
      setCheckedList(defaultCheckedList);
    }
  };

  const onCheckAllChange = async (e) => {
    setCurrent(1);
    setCheckedList([]);
    setState(plainOptions);
    setCheckAll(e.target.checked);
  };

  const content = (
    <div style={{ display: 'list-item' }}>
      <Checkbox
        id="check-box"
        defaultChecked={defaultCheckedList}
        onChange={onCheckAllChange}
        checked={checkAll}
        className="checkbox-filter"
      >
        All
      </Checkbox>
      <CheckboxGroup
        id="check-box-group"
        options={plainOptions}
        value={checkedList}
        onChange={onChange}
        className="checkbox-filter"
      />
    </div>
  );

  const FilterByJoinedDate = (date) => {
    setAssigndate(date ? date.format('DD/MM/YYYY') : '');
  };

  const FilterBySearch = (data) => {
    setCurrent(1);
    setGetSearchData(data);
  };

  const [field, setField] = useState();
  const [order, setOrder] = useState();

  function onChangeSortOrder(_pagination, _filters, sorter, _extra) {
    setField(sorter.field);
    setOrder(sorter.order);
  }

  const title = (title, dataIndex) => {
    return (
      <div id="frame">
        <span>
          {title} {order === 'ascend' && field === dataIndex ? <CaretUpOutlined /> : <CaretDownOutlined />}
        </span>
      </div>
    );
  };

  const columns = [
    {
      width: '7%',
      title: title('No.', 'no'),
      dataIndex: 'no',
      key: 'no',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.no - b.no,
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </div>
      ),
    },
    {
      width: '16%',
      title: title('Asset Code', 'assetCode'),
      dataIndex: 'assetCode',
      ellipsis: true,
      key: 'assetcode',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </div>
      ),
    },
    {
      width: '23%',
      title: title('Asset Name', 'assetName'),
      dataIndex: 'assetName',
      key: 'assetname',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </div>
      ),
    },
    {
      width: '16%',
      title: title('Assigned to', 'userAssignedTo'),
      dataIndex: 'userAssignedTo',
      key: 'userassignedto',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.userAssignedTo.localeCompare(b.userAssignedTo),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </div>
      ),
    },
    {
      width: '16%',
      title: title('Assigned by', 'userAssignedBy'),
      dataIndex: 'userAssignedBy',
      key: 'userassignedby',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.userAssignedBy.localeCompare(b.userAssignedBy),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </div>
      ),
    },
    {
      width: '16%',
      title: title('Assigned Date', 'assignedDate'),
      dataIndex: 'assignedDate',
      key: 'assigneddate',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) =>
        convertDate.formatDate(convertDate.convertStrDate(a.assignedDate)) -
        convertDate.formatDate(convertDate.convertStrDate(b.assignedDate)),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {convertDate.convertStrDate(text)}
        </div>
      ),
    },
    {
      width: '23%',
      title: title('State', 'status'),
      dataIndex: 'status',
      key: 'status',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.status.localeCompare(b.status),
      render: (text, record) => (
        <div className="assignment-details col-btn" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {convertEnum.toShow(text)}
        </div>
      ),
    },
    {
      width: '120px',
      align: 'left',
      key: 'options',
      dataIndex: 'status',
      title: '',
      render: (_text, record) => {
        return (
          <div className="action">
            <Button
              className="edit-icon"
              data-id={record.assetCode}
              type="link"
              icon={<EditIcon />}
              disabled={record.status === 'WAITING_FOR_ACCEPTANCE' ? false : true}
              onClick={() => onClickToEdit(record.id)}
            ></Button>
            <Button
              className="delete-icon"
              onClick={onClickToAbleDelete}
              data-id={record.id}
              type="link"
              icon={<CloseCircleOutlined style={{ color: 'red' }} />}
              disabled={record.status === 'DECLINED' || record.status === 'WAITING_FOR_ACCEPTANCE' ? false : true}
            ></Button>
            <Button
              className="manage-assignment__return-icon"
              onClick={() => onClickToAbleRequestReturn(record.id)}
              type="link"
              icon={<RefreshIcon />}
              disabled={record.status === 'ACCEPTED' && record.returnAsset === null ? false : true}
            ></Button>
          </div>
        );
      },
    },
  ].filter((item) => !item.hidden);

  return (
    <div className="manage-assignment">
      <h1 className="manage-assignment__title">Assignment List</h1>
      <div className="manage-assignment__function">
        <Popover content={content} placement="bottom" trigger="click" overlayClassName="function--popover">
          <Button className="function--button">
            <Row>
              <Col span={21}>State</Col>
              <Col span={1} className="border-right"></Col>
              <Col span={2}>
                <FilterIcon type="filter" />
              </Col>
            </Row>
          </Button>
        </Popover>
        <DatePicker
          id="manage-assignment__date-picker"
          className="manage-assignment-date"
          placeholder="Assigned Date"
          format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
          onChange={FilterByJoinedDate}
        />
        <Search
          id="manage-assignment__search"
          maxLength={100}
          className="manage-assignment-search"
          allowClear
          onSearch={FilterBySearch}
        ></Search>
        <Button
          className="manage-assignment-button"
          onClick={() => navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}/${adminRoute.createAssignment}`)}
        >
          Create new assignment
        </Button>
      </div>
      <br></br>
      <div className="manage-assignment__body">
        {isLoading ? (
          <Space size="middle">
            <Spin size="large" style={{ paddingLeft: '30rem', paddingTop: '10rem' }} />
          </Space>
        ) : (
          <Table
            id="manage-assignment__table"
            showSorterTooltip={false}
            size="small"
            sortDirections={'ascend'}
            pagination={false}
            className="user-list"
            dataSource={assignmentList}
            columns={columns}
            onChange={onChangeSortOrder}
          />
        )}
        <CustomPagination onChange={setCurrent} current={current} total={totalRow}></CustomPagination>

        <Modal
          centered
          className="assignment-details__modal"
          mask={false}
          title={'Detailed Assignment Information'}
          open={isModalAssignmentDetails}
          onOk={ModalAssignmentDetails}
          onCancel={ModalAssignmentDetails}
          footer={null}
          closeIcon={<CloseIcon className="manage-assignment__icon" />}
        >
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Asset Code</div>
            </Col>

            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.assetCode ?? 'Loading...'}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Asset Name</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.assetName ?? 'Loading...'}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Specification</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.specification ?? ''}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Assigned to</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.userAssignedTo ?? 'Loading...'}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Assigned by</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.userAssignedBy ?? 'Loading...'}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Assigned Date</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">
                {convertDate.convertStrDate(assignmentDetails?.assignedDate) ?? 'Loading...'}
              </div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">State</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">
                {!assignmentDetails?.status ? 'Loading...' : convertEnum.toShow(assignmentDetails?.status)}
              </div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">Note</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">{assignmentDetails?.note ?? ''}</div>
            </Col>
          </Row>
        </Modal>

        <Modal
          open={confirmPopUp}
          className="user-list__disable-modal"
          title={'Are you sure?'}
          centered
          onOk={onClickToDelete}
          onCancel={() => setConfirmPopUp(false)}
          okText="Delete"
          cancelText="Cancel"
          closable={false}
        >
          <p>Do you want to delete this assignment?</p>
        </Modal>
        <Modal
          open={requestReturnPopUp}
          className="user-list__disable-modal"
          title={'Are you sure?'}
          centered
          onOk={onClickToRequestReturn}
          onCancel={() => setRequestReturnPopUp(false)}
          okText="Yes"
          cancelText="No"
          closable={false}
        >
          <p>Do you want to create a returning request for this asset?</p>
        </Modal>
      </div>
    </div>
  );
};

export default ManageAssignment;
