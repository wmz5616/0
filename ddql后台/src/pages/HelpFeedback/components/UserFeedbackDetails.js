import React from 'react';
import { Modal, Form, Input, Select, Radio, DatePicker, Upload, message, Button } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import CKEditor from 'react-ckeditor-wrapper';
const { TextArea } = Input;
const { Option } = Select;
import moment from 'moment';
import { history, connect } from 'umi';
const { RangePicker } = DatePicker;
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
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
        //列表
        const { edit } = this.props;
        //问题类型
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: edit.id,
          },
          url: `/api/admin/feedback/info`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            if (res && res.code === 200) {
              this.setState({
                edits: res.data,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  handleOk = () => {
    const {  } = this.props;

    this.setState({
      isModalVisible: true,
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  handleOks = () => {
    const { handleOk, dispatch, getData, add, edit } = this.props;

    this.formRef.current.validateFields().then((values) => {
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: edit.id,
          remark: values.remark,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/feedback/process`,
        myData: (res) => {
          if (res.code == 200) {
            message.success(res.message);

            //用于更新消息角标显示
            this.props.dispatch({
              type: 'myModel/getSetData',
              payload: {
                limit: 999,
                status: 0,
              },
              url: `/api/admin/feedback/lists`,
              method: 'GET',
              myData: (res) => {
                if (res && res.code === 200) {
                  console.log(res.data.count);
                  this.props.dispatch({
                    type: 'global/save',
                    payload: {
                      countss: res.data.count,
                    },
                  });
                }
              },
            });
            //用于更新消息角标显示
            this.props.dispatch({
              type: 'myModel/getSetData',
              payload: {
                limit: 999,
                status: 1,
              },
              url: `/api/admin/order/refund/lists`,
              method: 'GET',
              myData: (res) => {
                if (res && res.code === 200) {
                  this.props.dispatch({
                    type: 'global/save',
                    payload: {
                      counts: res.data.count,
                    },
                  });
                }
              },
            });

            this.setState({
              isModalVisible: true,
            });
            handleOk();
            getData();
          }
        },
      });
    });
  };

  handleCancels = () => {
    this.setState({
      isModalVisible: false,
    });
  };

  showModalzz = (e) => {
    this.setState({
      xxxx: true,
      ccc: e,
    });
  };

  handleOkzz = (e) => {
    this.setState({
      xxxx: false,
    });
  };

  handleCancelzz = (e) => {
    this.setState({
      xxxx: false,
    });
  };

  render() {
    const { edits = {}, isModalVisible, listss = [] } = this.state;
    const { images = [] } = edits;
    return (
      <>
        <Modal
          title="反馈详情"
          visible
          onCancel={this.handleCancel}
          footer={
            edits.status !== 0 ? (
              <Button key="back" type="primary" onClick={this.handleCancel}>
                确定
              </Button>
            ) : (
              [
                <Button key="back" onClick={this.handleCancel}>
                  取消
                </Button>,
                <Button key="submit" type="primary" onClick={this.handleOk}>
                  已处理
                </Button>,
              ]
            )
          }
        >
          <p style={{ marginLeft: 74 }}>ID：{edits.id}</p>
          <p style={{ marginLeft: 18 }}>意见和问题：{edits.content}</p>
          <p style={{ marginLeft: 59 }}>
            {}
            图片：
            <>
              {images[0] != '' && (
                <>
                  {images.map((res) => {
                    return (
                      <img
                        onClick={() => this.showModalzz(res)}
                        key={res}
                        src={`${res}`}
                        alt=""
                        style={{ width: 80, height: 80, objectFit: 'contain', marginRight: 10 }}
                      />
                    );
                  })}
                </>
              )}
            </>
          </p>
          <p style={{ marginLeft: 30 }}>联系方式：{edits.phone}</p>
          <p style={{ marginLeft: 58 }}>
            状态：{' '}
            <span>
              {edits.status === 0 ? (
                <span className="huangse">未处理</span>
              ) : (
                <span style={{ color: '#ccc' }}>已处理</span>
              )}
            </span>
          </p>
          <p style={{ marginLeft: 30 }}>反馈用户：{edits && edits.user && edits.user.username}</p>
          <p style={{ marginLeft: 30 }}>反馈时间：{edits.created_at}</p>

          {edits.status == 1 && (
            <>
              <p style={{ marginLeft: 43 }}>
                处理人：{edits.operator.username}({edits.operator.phone})
              </p>
              <p style={{ marginLeft: 58 }}>备注：{edits.remark}</p>
              <p style={{ marginLeft: 30 }}>处理时间：{edits.processed_at}</p>
            </>
          )}
        </Modal>

        <Modal
          title="已处理"
          visible={isModalVisible}
          onOk={this.handleOks}
          onCancel={this.handleCancels}
        >
          <Form ref={this.formRef}>
            <Form.Item label="备注" name="remark">
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>

        <Modal
          title="图片"
          visible={this.state.xxxx}
          onOk={this.handleOkzz}
          onCancel={this.handleCancelzz}
          width={800}
          footer={[
            <Button key="submit" type="primary" onClick={this.handleOkzz}>
              确定
            </Button>,
          ]}
        >
          <img
            src={`${this.state.ccc}`}
            alt=""
            style={{ width: '100%', height: '100%', objectFit: 'contain' }}
          />
        </Modal>
      </>
    );
  }
}

export default connect()(App);
