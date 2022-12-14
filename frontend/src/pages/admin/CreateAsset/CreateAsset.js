import { Button, DatePicker, Divider, Form, Input, Radio, Select, Space } from 'antd';
import { useEffect, useState } from 'react';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

import './CreateAsset.scss';

import { getAllCategories } from '../../../services/getApiService';
import { adminRoute } from '../../../routes/routes';
import { postCreateNewCategory, postCreateNewAsset } from '../../../services/createApiService';

const CreateAsset = () => {
  const { Option } = Select;
  const { TextArea } = Input;

  const [form] = Form.useForm();

  const [formCategory] = Form.useForm();

  const initialError = { help: '', status: '' };

  const navigate = useNavigate();
  const assetNameType = Form.useWatch('assetName', form);
  const specificationType = Form.useWatch('specification', form);
  const stateType = Form.useWatch('assetStatus', form);
  const installedDateType = Form.useWatch('installedDate', form);
  const categoryType = Form.useWatch('categoryName', form);
  const categoryNameType = Form.useWatch('categoryName', formCategory);
  const categoryPrefixType = Form.useWatch('prefixAssetCode', formCategory);

  const [isDisabled, setIsDisabled] = useState(true);
  const [assetNameValidate, setAssetNameValidate] = useState({ ...initialError });
  const [categoryNameValidate, setCategoryNameValidate] = useState({ ...initialError });
  const [categoryPrefixValidate, setCategoryPrefixValidate] = useState({ ...initialError });
  const [specificationValidate, setSpecificationValidate] = useState({ ...initialError });
  const [isAddCategory, setIsAddCategory] = useState(false);
  const [isDisabledAddCategory, setIsDisabledAddCategory] = useState(true);
  const [listCategories, setListCategories] = useState([]);

  useEffect(() => {
    document.title = 'Manage Asset - Create New Asset';
  }, []);

  useEffect(() => {
    if (
      assetNameValidate.help ||
      specificationValidate.help ||
      !assetNameType ||
      !stateType ||
      !installedDateType ||
      !specificationType ||
      !categoryType
    ) {
      setIsDisabled(true);
      return;
    }
    setIsDisabled(false);
  }, [assetNameType, stateType, installedDateType, specificationType, categoryType]);

  useEffect(() => {
    if (!categoryNameType || !categoryPrefixType) {
      setIsDisabledAddCategory(true);
      return;
    }
    setIsDisabledAddCategory(false);
  }, [categoryNameType, categoryPrefixType]);

  useEffect(() => {
    fetchListCategories();
  }, []);

  const handleClickClose = () => {
    form.resetFields();
    navigate(`/${adminRoute.home}/${adminRoute.manageAsset}`);
  };

  const fetchListCategories = async () => {
    const response = await getAllCategories();
    if (response && response.status === 200) {
      setListCategories(response.data);
    }
  };

  const createAccountlayout = {
    labelCol: {
      span: 6,
    },
    wrapperCol: {
      span: 20,
    },
  };

  const handleSubmitCreateNewCategory = async (values) => {
    const response = await postCreateNewCategory({
      ...values,
    });
    if (response && response.status === 200) {
      formCategory.resetFields();
      fetchListCategories();
      setCategoryNameValidate({
        ...initialError,
      });
    } else {
      setCategoryNameValidate({
        help:
          response.response.data.message === 'Category is already existed. Please enter a different category'
            ? response.response.data.message
            : '' || response.response.data.message === 'Prefix is already existed. Please enter a different prefix'
            ? response.response.data.message
            : '',
        status: '',
      });
    }
  };

  const handleSubmitCreateNewAsset = async (values) => {
    setIsDisabled(true);
    const response = await postCreateNewAsset({
      ...values,
      assetName: values.assetName.trim(),
      installedDate: values.installedDate.format('DD/MM/YYYY'),
    });
    if (response && +response.status === 200) {
      form.resetFields();
      navigate(`/${adminRoute.home}/${adminRoute.manageAsset}`, {
        state: {
          assetResponse: response.data,
        },
      });
    }
    setIsDisabled(false);
  };

  const handleShowInputAddCategory = () => {
    setIsAddCategory(true);
  };

  const handleVisibleInputAddCategory = () => {
    setIsAddCategory(false);
    setCategoryNameValidate({ ...initialError });
    setCategoryPrefixValidate({ ...initialError });
    formCategory.resetFields();
  };

  const handleValidString = (event, type) => {
    if (event.target.value.trim().length === 0) {
      if (type === 'ASSET_NAME') {
        setAssetNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'SPECIFICATION') {
        setSpecificationValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'CATEGORY_NAME') {
        setCategoryNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'CATEGORY_PREFIX') {
        setCategoryNameValidate({
          help: '',
          status: '',
        });
        setCategoryPrefixValidate({
          help: '',
          status: 'error',
        });
        return;
      }
    }

    if (type === 'ASSET_NAME') {
      if (!/^([A-Za-z0-9\s])*[A-Za-z0-9\s]+$/.test(event.target.value)) {
        setAssetNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setAssetNameValidate({ ...initialError });
    }
    if (type === 'SPECIFICATION') {
      setSpecificationValidate({ ...initialError });
    }

    if (type === 'CATEGORY_NAME') {
      if (!/^[a-zA-Z0-9]+$/.test(event.target.value)) {
        setCategoryNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setCategoryNameValidate({ ...initialError });
    }
    if (type === 'CATEGORY_PREFIX') {
      if (!/^[a-zA-Z]+$/.test(event.target.value)) {
        setCategoryNameValidate({
          help: '',
          status: '',
        });
        setCategoryPrefixValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setCategoryNameValidate({ ...categoryNameType, help: '' });
      setCategoryPrefixValidate({ ...initialError });
    }
  };

  return (
    <div className="create-asset-block">
      <h3 className="create-asset__title">Create new Asset</h3>
      <Form
        {...createAccountlayout}
        initialValues={{
          assetName: '',
          assetStatus: 'AVAILABLE',
          joinedDate: '',
          specification: '',
        }}
        className="create-asset__form"
        form={form}
        name="control-hooks"
        onFinish={handleSubmitCreateNewAsset}
        labelAlign={'left'}
        colon={false}
      >
        <Form.Item name="assetName" label="Asset Name" help={assetNameValidate.help}>
          <Input
            id="create-asset-input__asset-name"
            className='create-asset-input__asset-name'
            onChange={(event) => handleValidString(event, 'ASSET_NAME')}
            status={assetNameValidate.status}
            maxLength={100}
          />
        </Form.Item>
        <Form.Item name="categoryName" label="Category">
          <Select
            id="create-asset-select__type"
            placeholder=""
            onDropdownVisibleChange={handleVisibleInputAddCategory}
            dropdownRender={(menu) => (
              <>
                {menu}
                {isAddCategory ? (
                  <>
                    <Divider className="create-asset__divider-line" />
                    <Form
                      {...createAccountlayout}
                      initialValues={{
                        categoryName: '',
                        categoryPrefix: '',
                      }}
                      form={formCategory}
                      name="control-hooks"
                      className="create-asset__form-category"
                      onFinish={handleSubmitCreateNewCategory}
                    >
                      <Space className="create-asset__space-form-category" style={{ paddingLeft: '0.5rem' }}>
                        <Form.Item
                          name="categoryName"
                          className="create-asset__input-text-category"
                          style={{ marginBottom: '0px' }}
                        >
                          <Input
                            placeholder="Bluetool Mouse"
                            style={{ width: '10rem' }}
                            status={categoryNameValidate.status}
                            id="create-category-name"
                            onChange={(event) => handleValidString(event, 'CATEGORY_NAME')}
                            maxLength={100}
                          />
                        </Form.Item>
                        <Form.Item name="prefixAssetCode" style={{ marginBottom: '0px' }}>
                          <Input
                            placeholder="BM"
                            style={{ maxWidth: '4rem' }}
                            id="create-category-prefix"
                            onChange={(event) => handleValidString(event, 'CATEGORY_PREFIX')}
                            status={categoryPrefixValidate.status}
                            maxLength={100}
                          />
                        </Form.Item>
                        <Button
                          htmlType="submit"
                          type="link"
                          danger
                          icon={<CheckOutlined />}
                          style={{ border: 'none' }}
                          disabled={isDisabledAddCategory}
                        ></Button>
                        <CloseOutlined style={{ minWidth: '1rem' }} onClick={handleVisibleInputAddCategory} />
                      </Space>
                      <div
                        className="create-asset__massage-category-error"
                        style={{ padding: '0.5rem', wordBreak: 'break-word', width: '17rem' }}
                      >
                        {categoryNameValidate.help}
                      </div>
                    </Form>
                  </>
                ) : (
                  <>
                    <Divider style={{ margin: '8px 0' }} />
                    <a onClick={handleShowInputAddCategory} className="create-asset__add-new-button">
                      Add new category
                    </a>
                  </>
                )}
              </>
            )}
          >
            {listCategories &&
              listCategories.map((item) => {
                return (
                  <Option key={item.prefixAssetCode} value={item.name}>
                    {item.name}
                  </Option>
                );
              })}
          </Select>
        </Form.Item>
        <Form.Item name="specification" label="Specification" help={specificationValidate.help}>
          <TextArea
            id="create-asset-specification"
            className='create-asset-specification'
            rows={4}
            placeholder=""
            maxLength={200}
            status={specificationValidate.status}
            onChange={(event) => handleValidString(event, 'SPECIFICATION')}
            style={{ maxHeight: '7rem', minHeight: '7rem' }}
          />
        </Form.Item>
        <Form.Item name="installedDate" label="Installed Date">
          <DatePicker
            id="create-asset-date-picker__joined-date"
            placeholder=""
            format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
          />
        </Form.Item>
        <Form.Item name="assetStatus" label="State">
          <Radio.Group id="create-asset-radio__gender" value={stateType} style={{ paddingTop: '0.4rem' }}>
            <Space direction="vertical">
              <Radio value={'AVAILABLE'}>Available</Radio>
              <Radio value={'NOT_AVAILABLE'}>Not available</Radio>
            </Space>
          </Radio.Group>
        </Form.Item>
        <div className="create-asset__form-action">
          <Button type="primary" htmlType="submit" disabled={isDisabled}>
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
export default CreateAsset;
