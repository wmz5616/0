import React from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Radio,
  DatePicker,
  Upload,
  Row,
  Col,
  TimePicker,
  InputNumber,
  message,
  Button,
  Table,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import moment from 'moment';
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        const { dispatch, recorda } = this.props;
        //管理员
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            page: this.state.pageNum,
            limit: 10,
            device_num: recorda.device_num,
            status: this.state.status,
            end_time: this.state.end_time && this.state.end_time,
            start_time: this.state.start_time && this.state.start_time,
          },
          url: `/api/admin/device/log`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState({
                listss: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, id, getData, add, edit } = this.props;

    handleOk();
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };

  onChange = (value, dateString) => {
    console.log('Formatted Selected Time: ', dateString);

    this.setState(
      {
        time_arr: dateString[0] !== '' && [`${dateString[0]} 00:00`, `${dateString[1]} 23:59`],
      },
      () => {
        // this.getData()
      },
    );
  };

  onFinish = (vae) => {
    this.setState(
      {
        status: vae.status,
        end_time: this.state.time_arr && this.state.time_arr[1],
        start_time: this.state.time_arr && this.state.time_arr[0],
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        status: undefined,
        end_time: undefined,
        start_time: undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const { recorda } = this.props;
    const { listss = [] } = this.state;
    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },

      {
        title: '设备状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span>
                {record.status === 1 ? (
                  <span className="luSe">在线</span>
                ) : (
                  <span className="red">离线</span>
                )}
              </span>
            </div>
          );
        },
      },
      {
        title: '更新时间',
        dataIndex: 'created_at',
      },
    ];

    return (
      <>
        <Modal
          title={`设备日志-编号：${recorda.device_num}`}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          footer={[
            <Button key="submit" type="primary" onClick={this.handleOk}>
              确定
            </Button>,
          ]}
        >
          <Form ref={this.formRef} onFinish={this.onFinish}>
            <Row gutter={16}>
              <Col className="gutter-row" span={8}>
                <Form.Item label="设备状态" name="status">
                  <Select allowClear placeholder="请选择">
                    <Option value={1}>在线</Option>
                    <Option value={0}>离线</Option>
                  </Select>
                </Form.Item>
              </Col>

              <Col className="gutter-row" span={8}>
                <Form.Item label="操作时间" name="created_at">
                  <RangePicker
                    style={{ width: '100%' }}
                    // showTime={{ format: 'HH:mm' }}
                    format="YYYY-MM-DD"
                    onChange={this.onChange}
                  />
                </Form.Item>
              </Col>

              <Col className="gutter-row" span={8} style={{ textAlign: 'right' }}>
                <Form.Item>
                  <Button type="primary" htmlType="submit">
                    查询
                  </Button>

                  <Button className="mL15" onClick={this.resets}>
                    重置
                  </Button>
                </Form.Item>
              </Col>
            </Row>
          </Form>

          <Table
            style={{ marginTop: 15 }}
            rowKey="id"
            columns={columns}
            dataSource={listss}
            pagination={{
              showSizeChanger: false,
              onChange: this.pageChange,
              pageSize: 10,
              total: this.state.total,
              current: this.state.pageNum,
            }}
            // scroll={{ x: '100%' }}
          />
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
