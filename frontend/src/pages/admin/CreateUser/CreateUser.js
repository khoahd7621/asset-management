import { Button, DatePicker, Form, Input, Radio, Select } from 'antd';
import moment from 'moment';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import './CreateUser.scss';

import { adminRoute } from '../../../routes/routes';
import { postCreateNewUser } from '../../../services/createApiService';

const CreateUser = () => {
  const navigate = useNavigate();
  const createAccountlayout = {
    labelCol: {
      span: 6,
    },
    wrapperCol: {
      span: 20,
    },
  };
  const initialError = { help: '', status: '' };
  const { Option } = Select;
  const [form] = Form.useForm();
  const firstNameType = Form.useWatch('firstName', form);
  const lastNameType = Form.useWatch('lastName', form);
  const dateOfBirthType = Form.useWatch('dateOfBirth', form);
  const genderType = Form.useWatch('gender', form);
  const joinedDateType = Form.useWatch('joinedDate', form);
  const typeType = Form.useWatch('type', form);
  const locationType = Form.useWatch('location', form);

  const [isDisabled, setIsDisabled] = useState(true);
  const [firstNameValidate, setfirstNameValidate] = useState({ ...initialError });
  const [lastNameValidate, setLastNameValidate] = useState({ ...initialError });
  const [dateOfBirthValidate, setDateOfBirthValidate] = useState({ ...initialError });
  const [joinedDateValidate, setJoinedDateValidate] = useState({ ...initialError });
  const [locationValidate, setLocationValidate] = useState({ ...initialError });

  useEffect(() => {
    document.title = 'Manage User - Create New User';
  }, []);

  useEffect(() => {
    if (
      firstNameValidate.help ||
      lastNameValidate.help ||
      dateOfBirthValidate.help ||
      joinedDateValidate.help ||
      !firstNameType ||
      !lastNameType ||
      !dateOfBirthType ||
      !genderType ||
      !joinedDateType ||
      !typeType
    ) {
      setIsDisabled(true);
      return;
    }
    if (typeType === 'ADMIN' && (!locationType || locationValidate.help)) {
      setIsDisabled(true);
      return;
    }
    setIsDisabled(false);
  }, [firstNameType, lastNameType, dateOfBirthType, genderType, joinedDateType, typeType, locationType]);

  const handleSubmitCreateNewUser = async (values) => {
    setIsDisabled(true);
    const response = await postCreateNewUser({
      ...values,
      dateOfBirth: values.dateOfBirth.format('DD/MM/YYYY'),
      joinedDate: values.joinedDate.format('DD/MM/YYYY'),
    });
    if (response && +response.status === 200) {
      resetFileds();
      navigate(`/${adminRoute.home}/${adminRoute.manageUser}`, {
        state: {
          userResponse: response.data,
        },
      });
    }
    setIsDisabled(false);
  };

  const handleClickClose = () => {
    resetFileds();
    navigate(`/${adminRoute.home}/${adminRoute.manageUser}`);
  };

  const resetFileds = () => {
    form.resetFields();
    setfirstNameValidate({ ...initialError });
    setLastNameValidate({ ...initialError });
    setDateOfBirthValidate({ ...initialError });
    setJoinedDateValidate({ ...initialError });
  };

  const handleValidString = (event, type) => {
    if (event.target.value.trim().length === 0) {
      if (type === 'FIRST_NAME') {
        setfirstNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'LAST_NAME') {
        setLastNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'LOCATION') {
        setLocationValidate({
          help: '',
          status: 'error',
        });
        return;
      }
    }
    if (event.target.value.trim().length > 100) {
      if (type === 'FIRST_NAME') {
        setfirstNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'LAST_NAME') {
        setLastNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'LOCATION') {
        setLocationValidate({
          help: '',
          status: 'error',
        });
        return;
      }
    }
    if (type === 'LOCATION') {
      if (!/^[a-zA-Z]+$/.test(event.target.value)) {
        setLocationValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setLocationValidate({ ...initialError });
    }
    if (type === 'FIRST_NAME') {
      if (!/^[a-zA-Z]+$/.test(event.target.value)) {
        setfirstNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setfirstNameValidate({ ...initialError });
    }
    if (type === 'LAST_NAME') {
      if (!/^([a-zA-Z]+\s)*[a-zA-Z]+$/.test(event.target.value)) {
        setLastNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setLastNameValidate({ ...initialError });
    }
  };

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
    <div className="create-user-block">
      <h3 className="create-user__title">Create new user</h3>
      <Form
        {...createAccountlayout}
        initialValues={{
          firstName: '',
          lastName: '',
          dateOfBirth: '',
          gender: 'FEMALE',
          joinedDate: '',
          type: '',
          location: '',
        }}
        className="create-user__form"
        form={form}
        name="control-hooks"
        onFinish={handleSubmitCreateNewUser}
        colon={false}
        labelAlign={'left'}
      >
        <Form.Item name="firstName" label="First Name" help={firstNameValidate.help}>
          <Input
            id="create-user-input__first-name"
            className='create-user-input__first-name'
            onChange={(event) => handleValidString(event, 'FIRST_NAME')}
            status={firstNameValidate.status}
            maxLength={100}
          />
        </Form.Item>
        <Form.Item name="lastName" label="Last Name" help={lastNameValidate.help}>
          <Input
            id="create-user-input__last-name"
            className='create-user-input__last-name'
            onChange={(event) => handleValidString(event, 'LAST_NAME')}
            status={lastNameValidate.status}
            maxLength={100}
          />
        </Form.Item>
        <Form.Item name="dateOfBirth" label="Date of Birth" help={dateOfBirthValidate.help}>
          <DatePicker
            id="create-user-date-picker__date-of-birth"
            placeholder="dd/mm/yyyy"
            format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
            onChange={handleValidateDateOfBirth}
            status={dateOfBirthValidate.status}
            disabledDate={(current) => {
              let customDate = moment('01/01/1900', 'DD/MM/YYYY');
              return current && current < moment(customDate, 'DD/MM/YYYY');
            }}
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
            id="create-user-date-picker__joined-date"
            placeholder="dd/mm/yyyy"
            format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
            onChange={handleValidateJoinedDate}
            status={joinedDateValidate.status}
            disabledDate={(current) => {
              let customDate = moment('01/01/1900', 'DD/MM/YYYY');
              return current && current < moment(customDate, 'DD/MM/YYYY');
            }}
          />
        </Form.Item>
        <Form.Item name="type" label="Type">
          <Select id="create-user-select__type" placeholder="">
            <Option value="STAFF">Staff</Option>
            <Option value="ADMIN">Admin</Option>
          </Select>
        </Form.Item>
        <Form.Item noStyle shouldUpdate={(prevValues, currentValues) => prevValues.type !== currentValues.type}>
          {({ getFieldValue }) =>
            getFieldValue('type') === 'ADMIN' ? (
              <Form.Item name="location" label="Location" help={locationValidate.help}>
                <Input
                  id="create-user-input__location"
                  onChange={(event) => handleValidString(event, 'LOCATION')}
                  status={locationValidate.status}
                />
              </Form.Item>
            ) : null
          }
        </Form.Item>
        <div className="create-user__form-action">
          <Button type="primary" danger htmlType="submit" disabled={isDisabled}>
            Save
          </Button>
          <Button htmlType="button" onClick={handleClickClose}>
            Cancel
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default CreateUser;
