import { Form, Input, DatePicker, Radio, Select, Button, Space, Spin } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { CaretDownOutlined } from '@ant-design/icons';
import { useSelector, useDispatch } from 'react-redux';
import moment from 'moment';

import './EditUser.scss';

import { adminRoute } from '../../../routes/routes';
import { CalendarIcon } from '../../../assets/CustomIcon';
import { postEditUser } from '../../../services/editApiService';
import { removeDataUserLogout } from '../../../redux/slice/userSlice';
import { getUserDetails } from '../../../services/getApiService';
import convertDate from '../../../utils/convertDateUtil';
import convertEnum from '../../../utils/convertEnumUtil';

const EditUser = () => {
  const navigate = useNavigate();
  const params = useParams();
  const [form] = Form.useForm();
  const { Option } = Select;
  const dispatch = useDispatch();
  const user = useSelector((state) => state.user.user);

  const joinedDateType = Form.useWatch('joinedDate', form);
  const genderType = Form.useWatch('gender', form);
  const dateOfBirthType = Form.useWatch('dateOfBirth', form);
  const typeType = Form.useWatch('type', form);

  const initialError = { help: '', status: '' };
  const [dateOfBirthValidate, setDateOfBirthValidate] = useState({ ...initialError });
  const [joinedDateValidate, setJoinedDateValidate] = useState({ ...initialError });
  const [isDisabled, setIsDisabled] = useState(true);
  const [userDetails, setUserDetails] = useState();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = 'Manage User - Edit User';
  }, []);

  useEffect(() => {
    ApiUserDetails();
  }, []);

  const ApiUserDetails = async () => {
    setIsLoading(true);
    const response = await getUserDetails(params.id);
    if (response && response.status === 200) {
      setUserDetails(response.data);
    } else {
      navigate(`/${adminRoute.home}/${adminRoute.manageUser}`);
    }
  };

  useEffect(() => {
    if (userDetails?.type) setIsLoading(false);
  }, [userDetails]);

  useEffect(() => {
    if (
      dateOfBirthValidate.help ||
      joinedDateValidate.help ||
      !dateOfBirthType ||
      !genderType ||
      !joinedDateType ||
      !typeType
    ) {
      setIsDisabled(true);
      return;
    }

    setIsDisabled(false);
  }, [dateOfBirthType, genderType, joinedDateType, typeType]);

  const handleSubmitEditUser = async (values) => {
    setIsDisabled(true);
    const response = await postEditUser({
      ...values,
      type: convertEnum.toGet(values.type),
      dateOfBirth: values.dateOfBirth.format('DD/MM/YYYY'),
      joinedDate: values.joinedDate.format('DD/MM/YYYY'),
      staffCode: userDetails.staffCode,
    });

    if (response.status === 200) {
      if (user.username === userDetails.username && values.type === 'STAFF') {
        dispatch(removeDataUserLogout());
      } else {
        resetFileds();
        navigate(`/${adminRoute.home}/${adminRoute.manageUser}`, {
          state: {
            userResponse: response.data,
          },
        });
      }
    }
    setIsDisabled(false);
  };

  const handleClickClose = () => {
    resetFileds();
    navigate(`/${adminRoute.home}/${adminRoute.manageUser}`);
  };

  const resetFileds = () => {
    form.resetFields();
    setDateOfBirthValidate({ ...initialError });
    setJoinedDateValidate({ ...initialError });
  };

  useEffect(() => {
    form.setFieldsValue({
      firstname: userDetails?.firstName,
      lastname: userDetails?.lastName,
      dateOfBirth: moment(userDetails?.dateOfBirth ? convertDate.convertStrDate(userDetails.dateOfBirth) : '', 'DD/MM/YYYY'),
      gender: userDetails?.gender,
      joinedDate: moment(userDetails?.joinedDate ? convertDate.convertStrDate(userDetails.joinedDate) : '', 'DD/MM/YYYY'),
      type: userDetails?.type,
      location: userDetails?.location,
    });
  }, [userDetails, form]);

  const handleValidateDateOfBirth = (date, _dateString) => {
    if (date) {
      if (date.diff(new Date(), 'years') > -18) {
        setDateOfBirthValidate({
          help: 'User is under 18. Please select a different date.',
          status: 'error',
        });
      } else {
        setDateOfBirthValidate({ ...initialError });
      }
      if (joinedDateType) {
        if (date.diff(joinedDateType, 'days') >= 0) {
          setJoinedDateValidate({
            help: 'Joined date is not later than Date of Birth. Please select a different date',
            status: 'error',
          });
        } else {
          setJoinedDateValidate({ ...initialError });
        }
      }
    }
  };

  const handleValidateJoinedDate = (date, _dateString) => {
    if (date) {
      const dayOfWeek = date.format('dddd');

      if (dayOfWeek === 'Saturday' || dayOfWeek === 'Sunday') {
        setJoinedDateValidate({
          help: 'Joined date is Saturday or Sunday. Please select a different date.',
          status: 'error',
        });
        return;
      }
      if (dateOfBirthType) {
        if (date.diff(dateOfBirthType, 'days') <= 0) {
          setJoinedDateValidate({
            help: 'Joined date is not later than Date of Birth. Please select a different date',
            status: 'error',
          });
          return;
        }
      }
      setJoinedDateValidate({ ...initialError });
    }
  };

  return (
    <>
      {isLoading ? (
        <Space size="middle">
          <Spin size="large" style={{ paddingLeft: '30rem' }} />
        </Space>
      ) : (
        <div className="edit-user">
          <h3 className="edit-user__title">Edit User</h3>
          <Form
            initialValues={{}}
            form={form}
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 20 }}
            layout="horizontal"
            className="edit-user__form"
            onFinish={handleSubmitEditUser}
            colon={false}
          >
            <Form.Item name="firstname" label="First Name" disable>
              <Input id="edit-user__first-name" disabled />
            </Form.Item>
            <Form.Item name="lastname" label="Last Name" disable>
              <Input id="edit-user__last-name" disabled />
            </Form.Item>
            <Form.Item name="dateOfBirth" label="Date of Birth" help={dateOfBirthValidate.help}>
              <DatePicker
                id="edit-user-date-picker__date-of-birth"
                placeholder="dd/mm/yyyy"
                format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
                onChange={handleValidateDateOfBirth}
                status={dateOfBirthValidate.status}
                suffixIcon={<CalendarIcon />}
              />
            </Form.Item>
            <Form.Item name="gender" label="Gender">
              <Radio.Group id="create-user-radio__gender" value={genderType}>
                <Radio value={'FEMALE'}>Female</Radio>
                <Radio value={'MALE'}>Male</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item name="joinedDate" label="Joined Date" help={joinedDateValidate.help}>
              <DatePicker
                id="edit-user-date-picker__joined-date"
                placeholder="dd/mm/yyyy"
                format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
                onChange={handleValidateJoinedDate}
                status={joinedDateValidate.status}
                suffixIcon={<CalendarIcon />}
              />
            </Form.Item>
            <Form.Item name="type" label="Type">
              <Select id="edit-user-select__type" placeholder="" bordered={false} suffixIcon={<CaretDownOutlined />}>
                <Option value="STAFF">Staff</Option>
                <Option value="ADMIN">Admin</Option>
              </Select>
            </Form.Item>
            <Form.Item noStyle shouldUpdate={(prevValues, currentValues) => prevValues.type !== currentValues.type}>
              {({ getFieldValue }) =>
                getFieldValue('type') === 'ADMIN' ? (
                  <Form.Item name="location" label="Location">
                    <Input id="create-user-input__location" disabled />
                  </Form.Item>
                ) : null
              }
            </Form.Item>
            <Form.Item>
              <div className="edit-user__form-action">
                <Button type="primary" danger htmlType="submit" disabled={isDisabled}>
                  Save
                </Button>
                <Button htmlType="button" onClick={handleClickClose}>
                  Cancel
                </Button>
              </div>
            </Form.Item>
          </Form>
        </div>
      )}
    </>
  );
};

export default EditUser;
