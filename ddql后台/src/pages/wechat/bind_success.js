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
      wechat_id:wechat_id,
    })
  }



  xxx=(v)=>{
    const { dispatch } = this.props;
    window.location.href = `/wechat/unbind?wechat_id=${this.state.wechat_id}`
  }

  render() {
    return (
      <div style={{ padding: 25, backgroundColor: '#f5f5f5', height: '100%' }}>
        <div style={{ padding: 25, backgroundColor: '#fff' }}>
          
          <div style={{textAlign:'center',color:'#52c41a',fontSize:42}} >
          <CheckCircleOutlined />
          {this.state.wechat_id?  <p style={{fontSize:24}}>
           绑定成功
          </p>: <p style={{fontSize:24}}>
           解绑成功
          </p>}
         
          {this.state.wechat_id&& <a onClick={this.xxx} style={{fontSize:14}}>解绑微信</a>}
         
          </div>
        
      
        </div>
      </div>
    );
  }
}

export default connect()(Loginx);
