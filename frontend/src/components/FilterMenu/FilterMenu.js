import React, { useEffect, useState } from 'react';
import { Checkbox, Popover, Button } from 'antd';
import { FilterOutlined, FilterFilled } from '@ant-design/icons';

import './FilterMenu.scss';

const CheckboxRender = ({ options, onChange, selectedItems }) => {
  return (
    <Checkbox.Group onChange={onChange} value={selectedItems}>
      {options.map((label, index) => (
        <Checkbox key={`${label}-${index}`} value={label} style={{ display: 'block', margin: '0' }}>
          {label}
        </Checkbox>
      ))}
    </Checkbox.Group>
  );
};

const FilterMenu = ({
  options = ['All'],
  value = ['All'],
  onChange,
  checkboxType = 'Checkbox',
  title = 'Checkbox',
  ...props
}) => {
  const [Icon, setIcon] = useState(FilterOutlined);
  const [selectedItems, setSelectedItems] = useState([]);

  useEffect(() => {
    if (value && value.length) {
      setSelectedItems([...value]);
    }
  }, [value]);

  useEffect(() => {
    if (selectedItems.length) {
      setIcon(FilterFilled);
    } else {
      setIcon(FilterOutlined);
    }
  }, [selectedItems]);

  const handleOnChange = (selection) => {
    if (selection.length && options.length - 1 === selection.length) {
      if (selectedItems.some((item) => item === 'All') && selection.some((item) => item === 'All')) {
        const arrSelections = selection.filter((item) => item !== 'All');
        setSelectedItems([...arrSelections]);
        onChange(arrSelections, checkboxType);
      } else {
        setSelectedItems(['All']);
        onChange(['All'], checkboxType);
      }
    } else if (selection.length) {
      if (selectedItems.some((item) => item !== 'All') && selection.some((item) => item === 'All')) {
        setSelectedItems(['All']);
        onChange(['All'], checkboxType);
      } else {
        const arrSelections = selection.filter((item) => item !== 'All');
        setSelectedItems([...arrSelections]);
        onChange(arrSelections, checkboxType);
      }
    } else {
      setSelectedItems(['All']);
      onChange(['All'], checkboxType);
    }
  };

  return (
    <Popover
      content={<CheckboxRender options={options} onChange={handleOnChange} selectedItems={selectedItems} />}
      trigger="click"
      placement="bottomRight"
      overlayClassName="checkbox-popover"
      {...props}
    >
      <Button className="checkbox-menu__button">
        <p>{title}</p>
        <Icon />
      </Button>
    </Popover>
  );
};

export default FilterMenu;
