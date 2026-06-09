import React from 'react';
import { Modal, Form, Input, Select, Radio, DatePicker, Upload, message } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import CKEditor from 'react-ckeditor-wrapper';
const { TextArea } = Input;
const { Option } = Select;
import moment from 'moment';
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
        addUrl: edit.cover,
        imageUrl: edit.cover,
        content: edit.content, //
      });
      this.formRef.current.setFieldsValue({
        cover: edit.cover,
        title: edit.title,
        type: edit.type == '2' ? [2] : edit.type == '4' ? [4] : [2, 4],
        push_time: moment(edit.publish_time, 'YYYY-MM-DD HH:mm'),
        publish: edit.publish,
         content: this.state.content, //
      });
    }
  };

  handleOk = () => {
    const { handleOk, dispatch, getData, add, edit } = this.props;
    this.formRef.current.validateFields().then((values) => {
      if (add) {
        //新建
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            cover: this.state.addUrl, //封面
            title: values.title, //名称
            type: values.type == '2' ? '2' : values.type == '4' ? '4' : '6', //类型
            content: this.state.content, //
            push_time: values.push_time&&values.push_time.format('YYYY-MM-DD HH:mm'), //时间
            publish: values.publish,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/notice/add`,
          myData: (res) => {
            if (res.code == 200) {
              message.success(res.message);
              handleOk();
              getData();
            }else{
              message.error(res.message);
            }
          },
        });
      } else {
        //编辑
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: edit.id,
            cover: this.state.addUrl, //封面
            title: values.title, //名称
            type: values.type == '2' ? '2' : values.type == '4' ? '4' : '6', //类型
            content: this.state.content, //
            push_time: values.push_time ? values.push_time.format('YYYY-MM-DD HH:mm') : undefined, //时间
            publish: values.publish,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/notice/update`,
          myData: (res) => {
            if (res.code == 200) {
              message.success(res.message);
              handleOk();
              getData();
            }
          },
        });
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
      console.log(info);
      this.setState({
        addUrl: info.file.response.data.uri,
      });

      message.success({ content: '上传成功', duration: 0.7 });
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
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
          if (info.file.status == 'done') {
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
      <>
        <Modal
          title={add ? '新增通知' : '编辑通知'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef}  {...layout}>
            <Form.Item label="标题" name="title" rules={[{ required: true }]}>
              <Input placeholder="请输入"/>
            </Form.Item>

            <Form.Item label="类型" name="type"rules={[{ required: true,message:'请选择' }]}>
              <Select
                allowClear
                showSearch
                mode="multiple"
                placeholder="请选择"
                optionFilterProp="label"
              >
                <Option value={2}  label={`${2}场馆公告`} >场馆公告</Option>
                <Option value={4}  label={`${4}场地租赁公告`}>场地租赁公告</Option>
              </Select>
            </Form.Item>
            <Form.Item
              noStyle
              shouldUpdate={(prevValues, currentValues) => prevValues.publish !== currentValues.publish}
            >
              {({ getFieldValue }) => {
                const isPublishNow = getFieldValue('publish') === 1;
                return isPublishNow ? null : (
                  <Form.Item
                    label="发布时间"
                    name="push_time"
                    rules={[{ required: true, message: '请选择发布时间!' }]}
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

            <Form.Item label="立即发布" name="publish" rules={[{ required: true }]}initialValue={1}  >
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item
              label={
                <span>缩略图
                </span>
              }
            >
              <Form.Item noStyle >
                <Upload
                  name="file"
                  listType="picture-card"
                  className="avatar-uploader"
                  showUploadList={false}
                  action="/ddql/file/upload"
                  // headers={{ Authorization: getToken() }}
                  beforeUpload={this.beforeUpload}
                  onChange={this.handleChange}
                >
                  {imageUrl ? (
                    <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
                  ) : (
                    uploadButton
                  )}
                </Upload>
              </Form.Item>
              <span style={{ color: '#ccc' }}>建议尺寸232*160 px</span>
            </Form.Item>

           

            <Form.Item label={
                <span>
                  <span style={{ color: 'red' }}>*</span>正文
                </span>
              } rules={[{ required: true, message: '请输入!' }]}>
              <div style={{ position: 'relative' }}>
                <Upload
                  showUploadList={false}
                  accept={'image/*'}
                  // headers={{
                  //   Authorization: getToken()
                  //
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
