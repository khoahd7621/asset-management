import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Row, Col, Table, Modal, Button, Popover, Checkbox, DatePicker, Input } from 'antd';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { CloseCircleOutlined } from '@ant-design/icons';

import './ManageAssignment.scss';

import { filterAssignmentList } from '../../../services/findApiService';
import { CloseIcon, EditIcon, SortIcon, FilterIcon, RefreshIcon, CalendarIcon } from '../../../assets/CustomIcon';
import CustomPagination from '../../../components/Pagination/Pagination';
import { getAssignmentDetails } from '../../../services/getApiService';
import { adminRoute } from '../../../routes/routes';

const ManageAssignment = () => {
  const { Search } = Input;
  const user = useSelector((state) => state.user.user);
  const location = useLocation();
  const navigate = useNavigate();
  const [assignmentList, setAssignmentList] = useState([]);
  const [assignmentDetails, setAssignmentDetails] = useState();
  const [isModalAssignmentDetails, SetIsModalAssignmentDetails] = useState(false);
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
    getData();
  }, [current, assigndate, getSearchData, state]);

  const getData = async () => {
    const response = await filterAssignmentList(
      getSearchData,
      state.map(handleValueEnumResquest),
      assigndate,
      current - 1,
    );
    if (response.status === 200) {
      const assignmentResponse = location.state?.assignmentResponse;
      if (assignmentResponse) {
        let count = 1;
        setAssignmentList(
          response?.data?.data.reduce(
            (prev, current) => {
              count++;
              return [...prev, { ...current, no: count, key: current.id }];
            },
            [
              {
                ...assignmentResponse,
                no: count,
                key: assignmentResponse.id,
              },
            ],
          ),
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
      setAssignmentDetails(response.data);
      SetIsModalAssignmentDetails(true);
    }
  };

  const ModalAssignmentDetails = () => {
    setAssignmentDetails();
    SetIsModalAssignmentDetails(false);
  };

  const formatDate = (assignedDate) => {
    const initial = assignedDate.split(/\//);
    const newdate = new Date([initial[1], initial[0], initial[2]].join('/'));
    return newdate.getTime();
  };

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

  const onClickToEdit = (assignmentId) => {
    navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}/${adminRoute.editAssignment}/${assignmentId}`);
  };

  const onClickToUnableDelete = () => {};
  const onClickToAbleDelete = () => {};

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
    setGetSearchData(data);
  };

  // handle letter case
  const toTitle = function (txt) {
    const text = txt.replaceAll('_', ' ');
    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
  };

  const handleValueEnumResquest = function (str) {
    return str.split(' ').join('_').toUpperCase();
  };

  const title = (title) => {
    return (
      <div id="frame">
        <div>{title}</div>
        <div>
          <SortIcon />
        </div>
      </div>
    );
  };

  const columns = [
    {
      width: '4em',
      title: title('No.'),
      dataIndex: 'no',
      key: 'no',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.no - b.no,
    },
    {
      width: '8em',
      title: title('Asset Code'),
      dataIndex: 'assetCode',
      ellipsis: true,
      key: 'assetcode',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
    },
    {
      width: '11em',
      title: title('Asset Name'),
      dataIndex: 'assetName',
      key: 'assetname',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      render: (text, record) => (
        <a className="assignment-details" data-id={record.id} onClick={showAssignmentDetailsModal}>
          {text}
        </a>
      ),
    },
    {
      width: '8em',
      title: title('Assigned to'),
      dataIndex: 'userAssignedTo',
      key: 'userassignedto',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.userAssignedTo.localeCompare(b.userAssignedTo),
    },
    {
      width: '8em',
      title: title('Assigned by'),
      dataIndex: 'userAssignedBy',
      key: 'userassignedby',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.userAssignedBy.localeCompare(b.userAssignedBy),
    },
    {
      width: '8em',
      title: title('Assigned Date'),
      dataIndex: 'assignedDate',
      key: 'assigneddate',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => formatDate(a.assignedDate) - formatDate(b.assignedDate),
      render: (text) => convertStrDate(text),
    },
    {
      width: '12em',
      title: title('State'),
      dataIndex: 'status',
      key: 'status',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.status.localeCompare(b.status),
      render: (text) => toTitle(text),
    },
    {
      align: 'center',
      key: 'options',
      dataIndex: 'status',
      title: '',
      render: (text, record) => {
        return (
          <div id="frame">
            <div className="edit-icon">
              <Button
                data-id={record.assetCode}
                type="link"
                icon={<EditIcon />}
                disabled={record.status === 'WAITING_FOR_ACCEPTANCE' ? false : true}
                onClick={() => onClickToEdit(record.id)}
              ></Button>
            </div>
            <div className="delete-icon">
              <Button
                onClick={record.assignedTo === user?.username ? onClickToUnableDelete : onClickToAbleDelete}
                data-id={record.assetCode}
                type="link"
                icon={<CloseCircleOutlined style={{ color: 'red' }} />}
                disabled={record.status === 'DECLINED' || record.status === 'WAITING_FOR_ACCEPTANCE' ? false : true}
              ></Button>
            </div>
            <div className="manage-assignment__return-icon">
              <Button
                // onClick={record.username === user?.username ? onClickToCurrentUser : onClickToCheck}
                data-id={record.assetCode}
                type="link"
                icon={<RefreshIcon />}
                disabled={record.status === 'ACCEPTED' ? false : true}
              ></Button>
            </div>
          </div>
        );
      },
    },
    {
      title: title(''),
      dataIndex: 'id',
      key: 'id',
      hidden: true,
    },
  ].filter((item) => !item.hidden);

  return (
    <div className="manage-assignment">
      <h1 className="manage-assignment__title">Assignment List</h1>
      <div className="manage-assignment__fucntion">
        <Row
          gutter={{
            xs: 300,
            sm: 300,
            md: 250,
            lg: 75,
          }}
        >
          <Col className="gutter-row" span={6}>
            <Popover content={content} placement="bottom" trigger="click" overlayClassName="function--popover">
              <Button className="function--button">
                <Row>
                  <Col span={21}>Type</Col>
                  <Col span={1} className="border-right"></Col>
                  <Col span={2}>
                    <FilterIcon type="filter" />
                  </Col>
                </Row>
              </Button>
            </Popover>
          </Col>
          <Col className="gutter-row" span={6}>
            <DatePicker
              id="manage-assignment-date"
              className="manage-assignment-date"
              placeholder="dd/mm/yyyy"
              format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
              onChange={FilterByJoinedDate}
              suffixIcon={<CalendarIcon />}
            />
          </Col>
          <Col className="gutter-row" span={6}>
            <Search
              id="manage-assignment-search"
              maxLength={100}
              className="manage-assignment-search"
              allowClear
              onSearch={FilterBySearch}
            ></Search>
          </Col>
          <Col className="gutter-row" span={6}>
            <Button
              className="manage-assignment-button"
              onClick={() =>
                navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}/${adminRoute.createAssignment}`)
              }
            >
              Create new assignment
            </Button>
          </Col>
        </Row>
      </div>
      <br></br>
      <div className="manage-assignment__body">
        <Table
          id="assignment-table"
          showSorterTooltip={false}
          size="small"
          sortDirections={'ascend'}
          pagination={false}
          className="user-list"
          dataSource={assignmentList}
          columns={columns}
        />
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
              <div className="content">{assignmentDetails?.specification ?? 'Loading...'}</div>
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
              <div className="content">{convertStrDate(assignmentDetails?.assignedDate) ?? 'Loading...'}</div>
            </Col>
          </Row>
          <Row>
            <Col span={8} sm={5} md={6}>
              <div className="title">State</div>
            </Col>
            <Col span={16} sm={19} md={18}>
              <div className="content">
                {!assignmentDetails?.status ? 'Loading...' : toTitle(assignmentDetails?.status)}
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
      </div>
    </div>
  );
};

export default ManageAssignment;
