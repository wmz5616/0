import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Radio,
  Select,
  DatePicker,
  Tabs,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
const { TabPane } = Tabs;
import AddQuestion from './components/AddQuestion';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
//所有场馆
//所有场馆
//所有场馆
//所有场馆
class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        // spinning: true,
      },
      () => {
        //获取系统设置
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            group: 'basic',
          },
          url: `/api/admin/system/config`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              const object = {};
              Object.keys(res.data).map((resd) => {
                object[res.data[resd].name] = res.data[resd].value;
              });

              this.formRef.current.setFieldsValue(object);
            } else {
              message.error(res.message);
            }
          },
        });
      },
    );
  };


  onFinish = (vas) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        group: 'basic',
        comp_phone:vas.comp_phone,
        comp_show:vas.comp_show,
      },
      url: `/api/admin/system/config`,
      method: 'POST',
      myData: (res) => {
        console.log(res);
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          message.success(res.message);
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };


  resets = (vas) => {
    // this.formRef.current.resetFields();
    this.getData();
  };

  render() {

    return (
      <PageContainer  header={{
        title: ``,
      }}>
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: 24 }}>
          <h1 style={{ fontWeight: '600', fontSize: '18px' }}>投诉电话</h1>
            <Form ref={this.formRef} onFinish={this.onFinish}>
         
                <Form.Item label="投诉电话" name="comp_phone">
                  <Input placeholder="请输入" style={{width:300}} />
                </Form.Item>

                <Form.Item label="是否显示" name="comp_show" >
                  <Radio.Group>
                    <Radio value={'1'}>是</Radio>
                    <Radio value={'0'}>否</Radio>
                  </Radio.Group>
                </Form.Item>

                <Form.Item>
                  <Button type="primary" htmlType="submit">
                    保存
                  </Button>
                  <Button style={{marginLeft:15}} onClick={this.resets}>重置</Button>
                </Form.Item>
           
            </Form>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
