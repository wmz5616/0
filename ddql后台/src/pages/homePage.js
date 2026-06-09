import React from 'react';
import { CheckCircleOutlined} from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  DatePicker,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
// import { Chart } from 'bizcharts';
class Loginx extends React.Component {
  formRef = React.createRef();
  state = {
    ccc: 1,
    ddd:'绑定成功'
  };

  componentDidMount() {
    // this.getData()
  }

  xxx = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/api/wechat/official/oauth_url`,
      method: 'GET',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          // window.location.href = `${res.data.url}`;
          this.setState({
            ccc: 2,
          });
        } else {
          message.error(res.message);
        }
      },
    });
  };



  onFinish=(v)=>{
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        phone:v.phone,
        password:v.password,
        wechat_id:3,
      },
      url: `/api/wechat/official/bind`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          this.setState({
            ccc: 3,
            ddd:res.message
          });
        } else {
          message.error(res.message);
        }
      },
    });
  }

  render() {
    return (
      <div style={{ padding: 25, backgroundColor: '#f5f5f5', height: '100%' }}>
        <div style={{ padding: 25, backgroundColor: '#fff' }}>
          {this.state.ccc == 1 && (
            <div style={{ textAlign: 'center', width: '100%' }}>
              <Button type="primary" style={{ width: '70%' }} onClick={this.xxx}>
                获取微信授权
              </Button>
            </div>
          )}

          {this.state.ccc==2 && (
            <Form
              name="basic"
              initialValues={{ remember: true }}
              onFinish={this.onFinish}
              autoComplete="off"
            >
              <Form.Item
                label="账号"
                name="phone"
                rules={[{ required: true, message: '请输入要绑定的账号' }]}
              >
                <Input />
              </Form.Item>

              <Form.Item
                label="密码"
                name="password"
                rules={[{ required: true, message: '请输入账号登录密码' }]}
              >
                <Input.Password />
              </Form.Item>

              <Form.Item style={{ width: '100%', textAlign: 'center' }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  style={{ textAlign: 'center', width: '70%' }}
                >
                  确定绑定
                </Button>
              </Form.Item>
            </Form>
          )}


          {this.state.ccc==3&&<>

          <div style={{textAlign:'center',color:'#52c41a',fontSize:42}} >
          <CheckCircleOutlined />
          <p style={{fontSize:24}}>
            {this.state.ddd}
          </p>
          </div>
        
          </>}
        </div>
      </div>
    );
  }
}

export default connect()(Loginx);
