import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router-dom';
import { Table, Row, Col, Modal, Input, Button, Checkbox, Popover, Space, Spin } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';

import { getItems } from '../../services/findApiService';
import { adminRoute } from '../../routes/routes';

import { CloseIcon, EditIcon, SortIcon, FilterIcon } from '../../assets/CustomIcon';
import Paging from './HandlePaging';

import './ListUser.scss';
import { checkValid, disableUser } from '../../services/disableApiService';

const ListUser = () => {
  const user = useSelector((state) => state.user.user);
  const location = useLocation();
  const navigate = useNavigate();
  const { Search } = Input;
  const [userList, setUserList] = useState([]);
  const [current, setCurrent] = useState(1);
  const [type, setType] = useState(['ALL']);
  const [userDetail, setUserDetail] = useState({});
  const [disabled, setDisable] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // Handle Filter
  const CheckboxGroup = Checkbox.Group;
  const plainOptions = ['Admin', 'Staff'];
  const defaultCheckedList = ['ALL'];
  const [checkedList, setCheckedList] = useState([]);
  const [checkAll, setCheckAll] = useState(true);
  const [isFilter, setIsFilter] = useState(false);
  const [isSearch, setIsSearch] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const [isModalVisible, setIsModalVisible] = useState(false);

  const [confirmPopUp, setConfirmPopUp] = useState(false);
  const [validPopUp, setValidPopUp] = useState(false);
  const [disablePopUp, setDisablePopUp] = useState(false);
  const [valueStaffCode, setValueStaffCode] = useState();
  const [isDisabled, setIsDisable] = useState(false);

  const formatDate = (joineddate) => {
    const initial = joineddate.split(/\//);
    const newdate = new Date([initial[1], initial[0], initial[2]].join('/'));
    return newdate.getTime();
  };

  // Letter case
  const toUpper = function (str) {
    return str.toUpperCase();
  };
  const toTitle = function (txt) {
    return txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase();
  };

  const onChange = async (list) => {
    let url = `/api/find/filter/0?location=${user.location}`;
    setType(list.map(toUpper));
    setIsFilter(true);
    setCurrent(1);
    if (list.map(toUpper).some((data) => data === 'STAFF' || data === 'ADMIN') && list.length < 2) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&type=${list.map(toUpper)}&location=${
        user.location
      }&page=${current - 1}`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      setUserList(response.data);
    }
    setCheckedList(list);
    setCheckAll(list.length === plainOptions.length);
    if (list.length === plainOptions.length) {
      setType(['ADMIN', 'STAFF']);

      setCheckAll(true);
      setCheckedList(defaultCheckedList);
      const response = await getItems(
        `/api/find/search?name=${searchValue}&staffCode=${searchValue}&location=${user.location}&page=${current - 1}`,
      );
      if (response.status === 200) {
        setUserList(response.data);
      }
    }
  };

  const onCheckAllChange = async (e) => {
    setCurrent(1);
    setCheckedList([]);
    setIsFilter(false);
    setType(['ADMIN', 'STAFF']);
    let url = `/api/find/filter/0?location=${user.location}`;
    if (isSearch === true) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&location=${user.location}&page=${
        current - 1
      }`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      setUserList(response.data);
    }
    setCheckAll(e.target.checked);
  };

  const content = (
    <div style={{ display: 'list-item' }}>
      <Checkbox
        defaultChecked={defaultCheckedList}
        onChange={onCheckAllChange}
        checked={checkAll}
        className="checkbox-filter"
      >
        All
      </Checkbox>
      <CheckboxGroup options={plainOptions} value={checkedList} onChange={onChange} className="checkbox-filter" />
    </div>
  );

  useEffect(() => {
    setIsLoading(true);
    getData();
  }, [current, isDisabled]);

  const getData = async () => {
    let url = `/api/find?location=${user.location}&pageNumber=${current - 1}`;
    if (isFilter === true && isSearch === false) {
      if (type.length < 2) {
        url = `/api/find/filter/${current - 1}?type=${type.map(toUpper)}&location=${user.location}`;
      } else {
        url = `/api/find/filter/${current - 1}?location=${user.location}`;
      }
    }
    if (isFilter === true && isSearch === true) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&type=${type}&location=${
        user.location
      }&page=${current - 1}`;
    }
    if (isFilter === false && isSearch === true) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&location=${user.location}&page=${
        current - 1
      }`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      const userResponse = location.state?.userResponse;
      if (userResponse && userResponse.location && userResponse.location === user.location) {
        const listDatas = response?.data.data.filter((item) => item.username !== userResponse.username);
        listDatas.unshift(userResponse);
        setUserList({
          ...response.data,
          data: listDatas,
        });
      } else {
        setUserList(response.data);
      }
      setIsLoading(false);
      window.history.replaceState({}, document.title);
    }
  };

  const ApiUserDetails = async (staffCode) => {
    const response = await getItems(`/api/find/get/${staffCode}`);
    if (response.status === 200) {
      setUserDetail(response.data);
    }
  };

  const onClickToEdit = (data) => {
    let lists = userList.data;
    let index = lists.findIndex((item) => item.staffCode === data.currentTarget.dataset.id);
    let editUser = lists[index];
    navigate(`/${adminRoute.home}/${adminRoute.manageUser}/${adminRoute.editUser}`, {
      state: {
        userDetails: editUser,
      },
    });
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
      width: '8em',
      title: title('Staff Code'),
      dataIndex: 'staffCode',
      key: 'staffcode',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.staffCode.match(/\d+/)[0] - b.staffCode.match(/\d+/)[0],
    },
    {
      width: '10em',
      title: title('Full Name'),
      dataIndex: 'fullName',
      ellipsis: true,
      key: 'fullname',
      defaultSortOder: 'ascend',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.fullName.localeCompare(b.fullName),
      render: (text, record) => (
        <a className="user-list" data-id={record.staffCode} onClick={showModal}>
          {text}
        </a>
      ),
    },
    {
      width: '10em',
      title: 'Username',
      dataIndex: 'username',
      ellipsis: true,
      key: 'username',
    },
    {
      width: '12em',
      title: title('Joined Date'),
      dataIndex: 'joinedDate',
      key: 'joineddate',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => formatDate(a.joinedDate) - formatDate(b.joinedDate),
    },
    {
      width: '5em',
      title: title('Type'),
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.type.localeCompare(b.type),
      render: (text) => toTitle(text),
    },
    {
      align: 'center',
      key: 'options',
      dataIndex: 'status',
      title: '',
      render: (text, record) => (
        <div id="frame">
          <div className="edit-icon">
            <Button
              data-id={record.staffCode}
              type="link"
              icon={<EditIcon />}
              disabled={disabled}
              onClick={onClickToEdit}
            ></Button>
          </div>
          <div></div>
          <div>
            <Button
              onClick={record.username === user?.username ? onClickToCurrentUser : onClickToCheck}
              data-id={record.staffCode}
              type="link"
              icon={<CloseCircleOutlined style={{ color: 'red' }} />}
            ></Button>
          </div>
        </div>
      ),
    },
    {
      title: title(''),
      dataIndex: 'firstName',
      key: 'firstname',
      hidden: true,
    },
    {
      title: title(''),
      dataIndex: 'lastName',
      key: 'lastname',
      hidden: true,
    },
    {
      title: title(''),
      dataIndex: 'gender',
      key: 'gender',
      hidden: true,
    },
    {
      title: title(''),
      dataIndex: 'dateOfBirth',
      key: 'datoOfbirth',
      hidden: true,
    },
  ].filter((item) => !item.hidden);

  const onClickToCheck = async (staffCode) => {
    setIsDisable(false);
    setValueStaffCode(staffCode.currentTarget.dataset.id);
    const response = await checkValid(staffCode.currentTarget.dataset.id);
    if (response.status === 200) {
      setConfirmPopUp(true);
    } else {
      setValidPopUp(true);
    }
  };

  const onClickToCurrentUser = async () => {
    setDisablePopUp(true);
  };

  const onClickToDisable = async () => {
    const response = await disableUser(valueStaffCode);
    if (response.status === 204) {
      setConfirmPopUp(false);
      setIsDisable(true);
    }
  };

  const showModal = (staffcode) => {
    ApiUserDetails(staffcode.currentTarget.dataset.id);
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const onSearch = async (value) => {
    setCurrent(1);
    let url = '';

    setIsSearch(true);
    setSearchValue(value);
    if (value === '') {
      setIsSearch(false);
    }
    if (type.some((data) => data === 'ALL') || type.length === 2 || type.length === 0) {
      url = `/api/find/search?name=${value}&staffCode=${value}&location=${user.location}&page=0`;
    } else {
      url = `/api/find/search?name=${value}&staffCode=${value}&type=${type}&location=${user.location}&page=0`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      setUserList(response.data);
    }
  };

  return (
    <div className="list-user-wrapper">
      <div>
        <Row>
          <Col xs={20} sm={4} md={6} lg={10} xl={11}>
            <Popover
              content={content}
              placement="bottom"
              trigger="click"
              overlayClassName="list-user-dropdown-box-type"
            >
              <Button className="handle-filter">
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
          <Col xs={20} sm={16} md={12} lg={10} xl={8}>
            <Search
              maxLength={100}
              className="handle-search"
              allowClear
              onSearch={onSearch}
              style={{ width: 220 }}
            ></Search>
          </Col>
          <Col xs={2} sm={4} md={6} lg={4} xl={4}>
            <Button
              className="handle-button"
              onClick={() => navigate(`/${adminRoute.home}/${adminRoute.manageUser}/${adminRoute.createUser}`)}
            >
              Create new user
            </Button>
          </Col>
        </Row>
      </div>
      <br></br>
      {isLoading ? (
        <Space size="middle">
          <Spin size="large" style={{ paddingLeft: '30rem', paddingTop: '10rem' }} />
        </Space>
      ) : (
        <>
          <Table
            showSorterTooltip={false}
            size="small"
            sortDirections={'ascend'}
            pagination={false}
            className="user-list"
            dataSource={userList.data}
            columns={columns}
          />
          <br></br>
          <div className="user-list">
            <Paging total={userList['totalRow']} current={current} onChange={setCurrent} />
          </div>
        </>
      )}
      <Modal
        centered
        className="user-list"
        mask={false}
        title={'Detailed User Information'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={null}
        closeIcon={<CloseIcon />}
      >
        <Row>
          <Col span={8}>
            <p>Staff Code</p>
            <p>Full Name</p>
            <p>Username</p>
            <p>Date of Birth</p>
            <p>Gender</p>
            <p>Joined Date</p>
            <p>Type</p>
            <p>Location</p>
          </Col>

          <Col span={16}>
            <p>{userDetail?.staffCode}</p>
            <p>{userDetail?.fullName}</p>
            <p>{userDetail?.username}</p>
            <p>{userDetail?.dateOfBirth}</p>
            <p>{!userDetail?.gender ? userDetail?.gender : toTitle(userDetail?.gender)}</p>
            <p>{userDetail?.joinedDate}</p>
            <p>{!userDetail?.type ? userDetail?.type : toTitle(userDetail?.type)}</p>
            <p>{userDetail?.location}</p>
          </Col>
        </Row>
      </Modal>

      <Modal
        className="user-list__valid-modal"
        title={'Can not disable user'}
        centered
        open={validPopUp}
        onCancel={() => setValidPopUp(false)}
        onOk={() => setValidPopUp(false)}
        footer={null}
        closeIcon={<CloseIcon />}
        mask={null}
      >
        <p>There are valid assignments belonging to this user.</p>
        <p>Please close all assignments before disabling user.</p>
      </Modal>

      <Modal
        open={confirmPopUp}
        className="user-list__disable-modal"
        title={'Are you sure?'}
        centered
        onOk={onClickToDisable}
        onCancel={() => setConfirmPopUp(false)}
        okText="Disable"
        cancelText="Cancel"
        closable={false}
      >
        <p>Do you want to disable this user?</p>
      </Modal>

      <Modal
        className="user-list__disable-modal"
        title={'Can not disable yourself'}
        centered
        open={disablePopUp}
        onCancel={() => setDisablePopUp(false)}
        onOk={() => setDisablePopUp(false)}
        footer={null}
        closeIcon={<CloseIcon />}
        mask={null}
      >
        <p>You can not disable your account</p>
      </Modal>
    </div>
  );
};

export default ListUser;
