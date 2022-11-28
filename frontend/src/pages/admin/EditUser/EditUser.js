import { Form, Input, DatePicker, Radio, Select, Button } from 'antd';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { adminRoute } from '../../../routes/routes';
import { CaretDownOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import './EditUser.scss';
import { CalendarIcon } from '../../../assets/CustomIcon';
import { postEditUser } from '../../../services/editApiService';
import moment from 'moment';

const EditUser = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { Option } = Select;
  const userDetails = location.state?.userDetails;
  const user = useSelector((state) => state.user.user);

  const joinedDateType = Form.useWatch('joinedDate', form);
  const genderType = Form.useWatch('gender', form);
  const dateOfBirthType = Form.useWatch('dateOfBirth', form);
  const typeType = Form.useWatch('type', form);

  const initialError = { help: '', status: '' };
  const [dateOfBirthValidate, setDateOfBirthValidate] = useState({ ...initialError });
  const [joinedDateValidate, setJoinedDateValidate] = useState({ ...initialError });
  const [isDisabled, setIsDisabled] = useState(true);

  useEffect(() => {
    window.history.replaceState({}, document.title);
  }, []);

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

  const toUpper = function (str) {
    return str.toUpperCase();
  };

  const handleSubmitEditUser = async (values) => {
    setIsDisabled(true);
    const response = await postEditUser({
      ...values,
      type: toUpper(values.type),
      dateOfBirth: values.dateOfBirth.format('DD/MM/YYYY'),
      joinedDate: values.joinedDate.format('DD/MM/YYYY'),
      staffCode: userDetails.staffCode,
    });
    if (response && +response.status === 200) {
      if (user.username === userDetails.username && values.type === 'STAFF') {
        navigate(`/`);
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

  const handleValidateDateOfBirth = (date, dateString) => {
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
        if (date.diff(joinedDateType, 'days') >= 6574) {
          setJoinedDateValidate({
            help: 'Joined date is under 18 year than Date of Birth. Please select a different date',
            status: 'error',
          });
        } 
         else {
          setJoinedDateValidate({ ...initialError });
        }
      }
    }
  };

  const handleValidateJoinedDate = (date, dateString) => {
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
        if (date.diff(dateOfBirthType, 'days') <= 6574 ) {
          setJoinedDateValidate({
            help: 'Joined date is under 18 year than Date of Birth. Please select a different date',
            status: 'error',
          });
          return;
        } 
        
      }
      setJoinedDateValidate({ ...initialError });
    }
  };

  return (
    <div className="edit-user">
      <h3 className="edit-user__title">Edit User</h3>
      <Form
        initialValues={{
          firstname: userDetails?.firstName,
          lastname: userDetails?.lastName,
          dateOfBirth: moment(userDetails?.dateOfBirth, 'DD/MM/YYYY'),
          gender: userDetails?.gender,
          joinedDate: moment(userDetails?.joinedDate, 'DD/MM/YYYY'),
          type: userDetails?.type,
        }}
        form={form}
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 20 }}
        layout="horizontal"
        className="edit-user__form"
        onFinish={handleSubmitEditUser}
      >
        <Form.Item name="firstname" label="First Name" disable>
          <Input disabled />
        </Form.Item>
        <Form.Item name="lastname" label="Last Name" disable>
          <Input disabled />
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
          <Select
            id="edit-user-select__type"
            placeholder=""
            allowClear
            bordered={false}
            suffixIcon={<CaretDownOutlined />}
          >
            <Option value="STAFF">Staff</Option>
            <Option value="ADMIN">Admin</Option>
          </Select>
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
  );
};

export default EditUser;
