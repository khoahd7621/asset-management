import { Button, DatePicker, Space } from 'antd';

function App() {
  return (
    <div className="App">
      <Space>
        <DatePicker />
        <Button type="primary">Primary Button</Button>
      </Space>
    </div>
  );
}

export default App;
