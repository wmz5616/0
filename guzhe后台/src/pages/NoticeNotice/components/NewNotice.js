import { getToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import {
  DatePicker,
  Form,
  Input,
  Modal,
  Radio,
  Select,
  Upload,
  message,
} from 'antd';
import dayjs from 'dayjs';
import moment from 'moment';
import React from 'react';
import CKEditor from 'react-ckeditor-wrapper';
const { TextArea } = Input;
const { Option } = Select;
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
    const { edit } = this.props;
    console.log(edit);
    if (edit) {
      this.setState({
        imageUrl: edit.cover,
        content: edit.content, //
      });
      this.formRef.current.setFieldsValue({
        cover: edit.cover,
        title: edit.title,
        type: edit.type == '2' ? 2 : 1,
        push_time: edit.publishTime
          ? dayjs(moment(edit.publishTime, 'YYYY-MM-DD HH:mm'))
          : undefined,
        publish: edit.isPublish ? 1 : 0,
        content: this.state.content,
      });
    }
  };

  handleOk = () => {
    const { handleOk, dispatch, getData, add, edit } = this.props;
    this.formRef.current.validateFields().then(async (values) => {
      if (add) {
        //新建
        const res = await post('/guzhe/notice/add', {
          cover: this.state.imageUrl, //封面
          title: values.title, //名称
          type: values.type, //类型
          content: this.state.content, //
          publishTime:
            values.push_time && values.push_time.format('YYYY-MM-DD HH:mm:ss'), //时间
          isPublish: values.publish,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          handleOk();
          getData();
        } else {
          message.error(res?.msg);
        }
      } else {
        //编辑
        const res = await post('/guzhe/notice/update', {
          id: edit.id,
          cover: this.state.imageUrl, //封面
          title: values.title, //名称
          type: values.type, //类型
          content: this.state.content, //
          publishTime: values.push_time
            ? values.push_time.format('YYYY-MM-DD HH:mm:ss')
            : undefined, //时间
          isPublish: values.publish,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          handleOk();
          getData();
        } else {
          message.error(res?.msg);
        }
      }
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  handleChange = (info) => {
    if (info.file.status == 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status == 'done') {
      console.log(info.file.response.data.url);
      this.setState({
        imageUrl: urlName + info.file.response.data.url,
      });

      message.success({ content: '上传成功', duration: 0.7 });
    }
  };

  // handleChanges(value) {
  //   this.setState({
  //     cc:sizeof(value)
  //   },()=>{
  //     console.log(this.state.cc)
  //   })
  // }
  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  render() {
    const { add } = this.props;

    const { loading, imageUrl } = this.state;
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/guzhe/file/upload',
        headers: { token: localStorage.getItem('token') },
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
          if (info.file.status == 'done') {
            console.log(info.file);
            const url = urlName + info.file.response.data.url;
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
      <>
        <Modal
          title={add ? '新增通知' : '编辑通知'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item label="标题" name="title" rules={[{ required: true }]}>
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item label="类型" name="type" rules={[{ required: true }]}>
              <Select placeholder="请选择">
                <Option value={1}>通知公告</Option>
                <Option value={2}>活动推广</Option>
              </Select>
            </Form.Item>
            <Form.Item
              label="立即发布"
              name="publish"
              rules={[{ required: true }]}
              initialValue={1}
            >
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              noStyle
              shouldUpdate={(prevValues, currentValues) => prevValues.publish !== currentValues.publish}
            >
              {({ getFieldValue }) => {
                const isPublishNow = getFieldValue('publish') === 1;
                return (
                  !isPublishNow && <Form.Item
                    label="发布时间"
                    name="push_time"
                    rules={[{ required: isPublishNow, message: '请选择发布时间!' }]}
                  >
                    <DatePicker
                      placeholder="请选择"
                      format="YYYY-MM-DD HH:mm"
                      showTime={{ format: 'HH:mm' }}
                    />
                  </Form.Item>
                );
              }}
            </Form.Item>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>正文
                </span>
              }
              rules={[{ required: true, message: '请输入!' }]}
            >
              <div style={{ position: 'relative' }}>
                <Upload
                  showUploadList={false}
                  accept={'image/*'}
                  headers={{
                    token: getToken(),
                  }}
                  {...uploadProps(1)}
                >
                  <div className="zxc" />
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
                        items: [
                          'Cut',
                          'Copy',
                          'Paste',
                          'PasteText',
                          'PasteFromWord',
                          '-',
                        ],
                      },
                      {
                        name: 'basicstyles',
                        items: [
                          'Bold',
                          'Italic',
                          'Underline',
                          '-',
                          'CopyFormatting',
                        ],
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
                    height: 250,
                    // uploadUrl: '/home/media/upload',
                    removeDialogTabs: 'image:advanced;link:advanced',
                  }}
                  onChange={this.updateContent}
                />
              </div>
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default App;
