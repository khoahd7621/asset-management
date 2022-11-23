import React from 'react';
import { Checkbox, Popover, Button, Row, Col } from 'antd';

import './ListUser.scss';

import { FilterIcon } from '../../assets/CustomIcon';

class CheckboxMenu extends React.Component {
  state = {
    icon: {},
    selectedItems: [],
  };

  componentDidMount = () => {
    if (this.props.value && this.props.value.length) {
      this.setState(
        {
          selectedItems: [...this.props.value],
        },
        () => this.checkIconFilled(),
      );
    }
  };

  onChange = (selection) => {
    this.setState({ selectedItems: [...selection] }, () => {
      this.checkIconFilled();
    });

    return this.props.onChange(selection);
  };

  checkIconFilled = () => {
    if (this.state.selectedItems.length) {
    
      console.log("Selected: " ,this.state.selectedItems)
      this.setState({ icon: { theme: 'filled' } });
    } else {
      this.setState({ icon: {} });
    }
  };

  checkboxRender = () => {
    const _this = this;
    const groups = this.props.options
      .map(function (e, i) {
        return i % 10 === 0 ? _this.props.options.slice(i, i + 10) : null;
      })
      .filter(function (e) {
        return e;
      });

    return (
      <Checkbox.Group onChange={this.onChange} value={this.state.selectedItems} className="checkbox-filter">
        <Row>
          {groups.map((group, i) => {
            return (
              <Col key={'checkbox-group-' + i} span={Math.floor(24 / groups.length)}>
                {group.map((label, i) => {
                  return (
                    <Checkbox key={label} value={label} style={{ display: 'flex', margin: '0' }}>
                      {label}
                    </Checkbox>
                  );
                })}
              </Col>
            );
          })}
        </Row>
      </Checkbox.Group>
    );
  };

  render() {
    const CheckboxRender = this.checkboxRender;
    return (
      <Popover
        overlayClassName="list-user-dropdown-box-type"
        content={<CheckboxRender />}
        trigger="click"
        placement="bottom"
      >
        <Button className="handle-filter">
          <Row>
            <Col span={21}>Type</Col>
            <Col span={1} className="border-right"></Col>
            <Col span={2}>
              <FilterIcon type="filter" {...this.state.icon} />
            </Col>
          </Row>
        </Button>
      </Popover>
    );
  }
}

export { CheckboxMenu };
