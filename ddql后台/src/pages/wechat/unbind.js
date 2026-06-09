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

    const { wechat_id } = this.props.location.query;
    this.setState({
      wechat_id:wechat_id
    },()=>{
      this.zz()
    })

  }

  zz=()=>{
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        wechat_id:this.state.wechat_id
      },
      url: `/api/wechat/official/bind_info`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          // window.location.href = `${res.data.url}`;
          this.formRef.current.setFieldsValue({
            phone:res.data.admin&&res.data.admin.phone
          })
        } else {
          message.error(res.message);
        }
      },
    });
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
        wechat_id:this.state.wechat_id,
      },
      url: `/api/wechat/official/unbind`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          window.location.href = `/wechat/bind_success`;
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
        <h1 style={{textAlign:'center'}}>微信解绑</h1>
            <Form
              name="basic"
              onFinish={this.onFinish}
              ref={this.formRef} 
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
                  确定解绑
                </Button>
              </Form.Item>
            </Form>
        </div>
      </div>
    );
  }
}

export default connect()(Loginx);
