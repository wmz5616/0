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
  Table,
  Select,
  Radio,
  DatePicker,
  Tabs,
  Upload,
  InputNumber,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { PageContainer } from '@ant-design/pro-layout';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { HTML5Backend } from 'react-dnd-html5-backend';
import CKEditor from 'react-ckeditor-wrapper';
import { history, connect } from 'umi';
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const { isOver, connectDragSource, connectDropTarget, moveRow, ...restProps } = this.props;
    const style = { ...restProps.style, cursor: 'move' };

    let { className } = restProps;
    if (isOver) {
      if (restProps.index > dragingIndex) {
        className += ' drop-over-downward';
      }
      if (restProps.index < dragingIndex) {
        className += ' drop-over-upward';
      }
    }

    return connectDragSource(
      connectDropTarget(<tr {...restProps} className={className} style={style} />),
    );
  }
}

const rowSource = {
  beginDrag(props) {
    dragingIndex = props.index;
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
}))(
  DragSource('row', rowSource, (connect) => ({
    connectDragSource: connect.dragSource(),
  }))(BodyRow),
);

class SecuritySettings extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
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
          payload: {},
          url: `/ddql/system/getSafeConfig`,
          method: 'post',
          myData: (res) => {
            console.log(res);
            if (res && res.code === 10000) {
              this.setState({
                listss: res.data,
                content: res.data.refundNotice,
                contents: res.data.queueRule,
              });

              this.formRef.current.setFieldsValue(res.data);
            } else {
              message.error(res.message);
            }
          },
        });
      },
    );
  };

  onFinish = (res) => {
    if (this.state.content) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: 1,
          refundNotice: this.state.content,
          queueRule: this.state.contents,
          keepTime: res.keepTime,
          orderCancelTime: res.orderCancelTime,
          cancelCount: res.cancelCount,
          lockReason: res.lockReason,
          refundTime: res.refundTime,
        },
        url: `/ddql/system/safeConfig/update`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            // this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    } else {
      message.error('请输入退款须知');
    }
  };

  resets = () => {
    this.getData();
  };
  updateContent = (value, index) => {
    this.setState({
      content: value,
    });
  };

  updateContents = (value, index) => {
    this.setState({
      contents: value,
    });
  };

  render() {
    const {
      list = [],
      selectedRowKeys,
      NewRoles,
      edit,
      listss = [],
      imageUrl,
      loading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    return (
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: 30 }}>
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>安全设置</h1>
            <Form
              ref={this.formRef}
              onFinish={this.onFinish}
              //  labelCol={{ span: 6 }}
              //   wrapperCol={{ span: 12 }}
              style={{ marginTop: 25 }}
            >
              <Form.Item
                style={{ marginLeft: 30 }}
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>订单叫号保留时间(分钟)
                  </span>
                }
              >
                <Form.Item name="keepTime" noStyle rules={[{ required: true, message: '请输入!' }]}>
                  <InputNumber min={0} />
                </Form.Item>
                <div style={{ color: '#ccc' }}>超过该时间的订单系统将自动取消</div>
              </Form.Item>

              <Form.Item
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>会员卡订单可支付时效(分钟)
                  </span>
                }
              >
                <Form.Item
                  name="orderCancelTime"
                  noStyle
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
                <div style={{ color: '#ccc' }}>超过该时间的会员卡订单系统将自动取消</div>
              </Form.Item>

              <Form.Item
                style={{ marginLeft: 50 }}
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>每天可取消订单数量
                  </span>
                }
              >
                <Form.Item
                  name="cancelCount"
                  noStyle
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
                <div style={{ color: '#ccc' }}>
                  为0即不限制，当天取消订单数量超过此数值后，该账号将被锁定，当天不允许再下单
                </div>
              </Form.Item>
              <Form.Item
                style={{ marginLeft: 115, width: 768 }}
                label="锁定原因"
                name="lockReason"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <TextArea rows={4} placeholder="请输入" />
              </Form.Item>
              <Form.Item
                style={{ marginLeft: 65 }}
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>*可退款时效(小时)
                  </span>
                }
              >
                <Form.Item
                  name="refundTime"
                  noStyle
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
                <div style={{ color: '#ccc' }}>场次开始使用时间-当前时间大于可退款时效时可退款</div>
              </Form.Item>

              <Form.Item
                style={{ marginLeft: 120 }}
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>退款须知
                  </span>
                }
                name="refundNotice"
                // rules={[{ required: true, message: '请输入!' }]}
              >
                <div style={{ position: 'relative', width: 700, marginTop: '-10px' }}>
                  <Upload
                    showUploadList={false}
                    accept={'image/*'}
                    // headers={{
                    //   Authorization: getToken()
                    //
                    {...uploadProps(1)}
                  >
                    <div className="zxcs" />
                  </Upload>
                  <CKEditor
                    ref={(ckeditor) => {
                      this.ckeditor = ckeditor;
                    }}
                    value={this.state.content}
                    config={{
                      toolbar: [
                        {
                          name: 'clipboard',
                          items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                        },
                        {
                          name: 'basicstyles',
                          items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
                        },
                        {
                          name: 'paragraph',
                          items: [
                            'NumberedList',
                            'BulletedList',
                            '-',
                            'Outdent',
                            'Indent',
                            '-',
                            'JustifyLeft',
                            'JustifyCenter',
                            'JustifyRight',
                            'JustifyBlock',
                            '-',
                          ],
                        },
                        { name: 'links', items: ['Link', 'Unlink'] },
                        { name: 'insert', items: ['Image', 'Table'] },
                        { name: 'styles', items: ['Font', 'FontSize'] },
                        { name: 'colors', items: ['TextColor', 'BGColor'] },
                        { name: 'tools', items: ['Maximize'] },
                      ],
                      extraPlugins: 'placeholder',
                      height: 450,
                      // uploadUrl: '/home/media/upload',
                      removeDialogTabs: 'image:advanced;link:advanced',
                    }}
                    onChange={this.updateContent}
                  />
                </div>
              </Form.Item>
              <Form.Item
                style={{ marginLeft: 120 }}
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>排队规则
                  </span>
                }
                name="refund_notice"
                // rules={[{ required: true, message: '请输入!' }]}
              >
                <div style={{ position: 'relative', width: 700, marginTop: '-10px' }}>
                  <Upload
                    showUploadList={false}
                    accept={'image/*'}
                    // headers={{
                    //   Authorization: getToken()
                    //
                    {...uploadProps(1)}
                  >
                    <div className="zxcs" />
                  </Upload>
                  <CKEditor
                    ref={(ckeditor) => {
                      this.ckeditor = ckeditor;
                    }}
                    value={this.state.contents}
                    config={{
                      toolbar: [
                        {
                          name: 'clipboard',
                          items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                        },
                        {
                          name: 'basicstyles',
                          items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
                        },
                        {
                          name: 'paragraph',
                          items: [
                            'NumberedList',
                            'BulletedList',
                            '-',
                            'Outdent',
                            'Indent',
                            '-',
                            'JustifyLeft',
                            'JustifyCenter',
                            'JustifyRight',
                            'JustifyBlock',
                            '-',
                          ],
                        },
                        { name: 'links', items: ['Link', 'Unlink'] },
                        { name: 'insert', items: ['Image', 'Table'] },
                        { name: 'styles', items: ['Font', 'FontSize'] },
                        { name: 'colors', items: ['TextColor', 'BGColor'] },
                        { name: 'tools', items: ['Maximize'] },
                      ],
                      extraPlugins: 'placeholder',
                      height: 450,
                      // uploadUrl: '/home/media/upload',
                      removeDialogTabs: 'image:advanced;link:advanced',
                    }}
                    onChange={this.updateContents}
                  />
                </div>
              </Form.Item>
              <Form.Item wrapperCol={{ offset: 3, span: 12 }}>
                <Button type="primary" htmlType="submit">
                  保存
                </Button>
                <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                  重置
                </Button>
              </Form.Item>
            </Form>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(SecuritySettings);
