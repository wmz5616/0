import React from 'react';
import { Modal, Form, Input, Select, Radio, DatePicker, Upload, message, Cascader } from 'antd';
import { ZoomInOutlined } from '@ant-design/icons';
const { TextArea } = Input;
const { Option } = Select;
import moment from 'moment';
import { BankingDetails, urlName } from '@/utils/utils';

import { history, connect } from 'umi';
const { RangePicker } = DatePicker;

class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
    operationTypes: '',
    effectiveType: '0',
    documentType: '0',
    ccc: [],
  };
  componentWillReceiveProps(props) {
    console.log(props);
    this.setState({
      effectiveType: String(props.effectiveType),
      IdPhotoB: props.IdPhotoB ? props.IdPhotoB : [],
      IdPhoto: props.IdPhoto ? props.IdPhoto : [],
      licenceUrl: props.licenceUrl ? props.licenceUrl : [],
      operationTypes: props.operationTypes,
      accType: this.props.formRef.current.getFieldValue('accType'),
      openAccountLicenceUrl: props.openAccountLicenceUrl ? props.openAccountLicenceUrl : [],
    });
    const { provincexx, bankNamexx } = props;

    if (provincexx) {
      const aa = BankingDetails.filter((res) => res.city == provincexx[1]);

      this.setState({
        dd: aa.filter((res) => res.bankName == bankNamexx),
      });
    }
  }
  componentDidMount() {
    const aaaMap = new Map();
    const aaasMap = new Map();
    BankingDetails.forEach((res) => {
      if (!aaasMap.has(res.bank)) {
        aaasMap.set(res.bank, res);
      }
      if (!aaaMap.has(res.provinceName)) {
        aaaMap.set(res.provinceName, {
          ...res,
          label: res.provinceName,
          value: res.province,
          key: res.province,
          childrenMap: new Map()
        });
      }

      const provinceItem = aaaMap.get(res.provinceName);
      if (res.marketName && !provinceItem.childrenMap.has(res.marketName)) {
        provinceItem.childrenMap.set(res.marketName, {
          marketName: res.marketName,



          city: res.city,
          label: res.marketName,
          value: res.city,
          key: res.city
        });
      }
    });

    const aaa = Array.from(aaaMap.values()).map(p => {
      p.children = Array.from(p.childrenMap.values());
      delete p.childrenMap;
      return p;
    });
    const aaas = Array.from(aaasMap.values());

    const dizhi = window.dizhi || [];
    const bbb = [];
    dizhi.map((res) => {
      if (!bbb.some((resd) => resd.label == res.province)) {
        bbb.push({ label: res.province, value: res.provinceCode });
      }
    });
    bbb.map((resd) => {
      const aa = dizhi.filter((res) => res.province == resd.label);
      const asdasdsa = [];
      aa.map((res) => {
        if (!asdasdsa.some((resd) => resd.label == res.market)) {
          asdasdsa.push({
            label: res.market,
            value: res.marketCode,
          });
        }
      });
      resd.children = asdasdsa;
    });
    bbb.map((res) => {
      res.children.map((resd) => {
        const asdasdsa = [];
        const aa = dizhi.filter((res) => res.market == resd.label);
        aa.map((res) => {
          asdasdsa.push({
            label: res.distinguish,
            value: res.distinguishCode,
          });
        });
        resd.children = asdasdsa;
      });
    });
    this.setState({
      ccc: aaa,
      bbb: bbb,
      aaas: aaas, //银行列表
    });

    // provincexx={this.state.provincexx}
    // bankBranchxx={this.state.bankBranchxx}
  }

  //营业执照照片

  onChangexx = (e) => {
    console.log(e);
    this.setState({
      licenceUrl: e.fileList,
    });
    const { response = {} } = e.file;
    if (e.file.status == 'done') {
      if (response.code == 10000) {
        this.setState(
          {
            licenceUrl: [
              {
                uid: '-1',
                name: 'image.png',
                status: 'done',
                url: urlName + response.data.url,
                thumbUrl: e.file.thumbUrl || (e.file.originFileObj ? URL.createObjectURL(e.file.originFileObj) : ''),
              },
            ],
          },
          () => {
            this.props.IDImage(
              this.state.IdPhoto,
              this.state.IdPhotoB,
              this.state.licenceUrl,
              this.state.openAccountLicenceUrl,
            );
          },
        );
        message.success(response.msg);
      } else {
        message.info(response.msg);
        this.setState({
          licenceUrl: [],
        });
      }
    }
  };

  onPreviewxx = async (file) => {
    let src = file.url;
    if (!src) {
      console.log(222);
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewOpen: true,
    });

    const image = new Image();
    image.src = src;
    const imgWindow = src;
    this.setState({
      imgWindows: imgWindow,
    });
    // imgWindow?.document.write(image.outerHTML);
  };

  handleCancel = () => {
    this.setState({
      previewOpen: false,
    });
  };

  //进件类型
  onChange = (e) => {
    this.setState({
      operationTypes: e.target.value,
    });
  };

  //执照/证书有效期判断
  onChanges = (e) => {
    this.props.formRef.current.setFieldsValue({
      businessBeginDate: undefined,
    });
    console.log(e.target.value);
    this.setState({
      effectiveType: e.target.value,
    });
  };

  //身份证正面照片
  onChangeA = (e) => {
    this.setState({
      IdPhoto: e.fileList,
    });
    const { response = {} } = e.file;
    if (e.file.status == 'done') {
      if (response.code == 10000) {
        this.setState(
          {
            IdPhoto: [
              {
                uid: '-1',
                name: 'image.png',
                status: 'done',
                url: urlName + response.data.url,
                thumbUrl: e.file.thumbUrl || (e.file.originFileObj ? URL.createObjectURL(e.file.originFileObj) : ''),
              },
            ],
          },
          () => {
            this.props.IDImage(
              this.state.IdPhoto,
              this.state.IdPhotoB,
              this.state.licenceUrl,
              this.state.openAccountLicenceUrl,
            );
          },
        );
        message.success(response.msg);
      } else {
        message.info(response.msg);
        this.setState({
          IdPhoto: [],
        });
      }
    }
  };

  onPreviewA = async (file) => {
    this.setState({
      imgWindows: this.state.IdPhoto[0].url,
      previewOpen: true,
    });
  };

  //身份证反面照片
  onChangeB = (e) => {
    this.setState({
      IdPhotoB: e.fileList,
    });
    const { response = {} } = e.file;
    if (e.file.status == 'done') {
      if (response.code == 10000) {
        this.setState(
          {
            IdPhotoB: [
              {
                uid: '-1',
                name: 'image.png',
                status: 'done',
                url: urlName + response.data.url,
                thumbUrl: e.file.thumbUrl || (e.file.originFileObj ? URL.createObjectURL(e.file.originFileObj) : ''),
              },
            ],
          },
          () => {
            this.props.IDImage(
              this.state.IdPhoto,
              this.state.IdPhotoB,
              this.state.licenceUrl,
              this.state.openAccountLicenceUrl,
            );
          },
        );
        message.success(response.msg);
      } else {
        message.info(response.msg);
        this.setState({
          IdPhotoB: [],
        });
      }
    }
  };

  onPreviewB = async (file) => {
    let src = file.url;
    if (!src) {
      console.log(222);
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewOpen: true,
    });

    const image = new Image();
    image.src = src;
    const imgWindow = src;
    this.setState({
      imgWindows: imgWindow,
    });
    // imgWindow?.document.write(image.outerHTML);
  };

  //证件有效期判断
  onChangeg = (e) => {
    this.props.formRef.current.setFieldsValue({
      cardBeginDate: undefined,
    });

    console.log(e.target.value);
    this.setState({
      documentType: e.target.value,
    });
  };

  //开户许可证照片

  onChangev = (e) => {
    this.setState({
      openAccountLicenceUrl: e.fileList,
    });
    const { response = {} } = e.file;
    if (e.file.status == 'done') {
      if (response.code == 10000) {
        this.setState(
          {
            openAccountLicenceUrl: [
              {
                uid: '-1',
                name: 'image.png',
                status: 'done',
                url: urlName + response.data.url,
                thumbUrl: e.file.thumbUrl || (e.file.originFileObj ? URL.createObjectURL(e.file.originFileObj) : ''),
              },
            ],
          },
          () => {
            this.props.IDImage(
              this.state.IdPhoto,
              this.state.IdPhotoB,
              this.state.licenceUrl,
              this.state.openAccountLicenceUrl,
            );
          },
        );
        message.success(response.msg);
      } else {
        message.info(response.msg);
        this.setState({
          openAccountLicenceUrl: [],
        });
      }
    }
  };

  onPreviewv = async (file) => {
    let src = file.url;
    if (!src) {
      console.log(222);
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewOpen: true,
    });

    const image = new Image();
    image.src = src;
    const imgWindow = src;
    this.setState({
      imgWindows: imgWindow,
    });
    // imgWindow?.document.write(image.outerHTML);
  };

  // // 地区
  area = (e) => {
    console.log(e);
    this.props.areaa(e);
  };

  getBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        return resolve(reader.result);
      };

      reader.onerror = (error) => reject(error);
    });

  banks = (e, ee) => {
    console.log(ee.children);
    this.setState(
      {
        bankNames: ee.children,
      },
      () => {
        this.props.bankDetails(this.state.bankNames);
      },
    );
  };

  Subbranch = (e, ee) => {
    console.log(e, ee);
    this.setState(
      {
        bankBranchs: ee.children,
      },
      () => {
        this.props.bankDetails(this.state.bankNames, this.state.bankBranchs);
      },
    );
  };

  AccountOpeningAddress = (e) => {
    console.log(this.state.bankNames, e);
    if (e) {
      const aa = BankingDetails.filter((res) => res.city == e[1]);
      console.log(aa);

      console.log(aa.filter((res) => res.bankName == this.state.bankNames));
      this.setState({
        dd: aa.filter((res) => res.bankName == this.state.bankNames),
      });
    }
  };

  render() {
    const {
      stadiumList = [],
      fileList = [],
      operationTypes,
      licenceUrl = [],
      IdPhoto = [],
      IdPhotoB = [],
      documentType,
      openAccountLicenceUrl = [],
      aaas = [],
      dd = [],
    } = this.state;
    const { Disables, Disablesss } = this.props;
    return (
      <>
        {/* <Form.Item
          label="选择场所"
          name="stadium_id"
          rules={[{ required: Disablesss, message: '请选择' }]}
        >
          <Select
            disabled={Disables}
            allowClear
            showSearch
            optionFilterProp="label"
            placeholder="请选择需要开通商户功能的场所"
          >
            {stadiumList.map((res) => {
              return (
                <Option value={res.id} key={res.id} label={`${res.id}${res.name}`}>
                  {res.name}
                </Option>
              );
            })}
          </Select>
        </Form.Item> */}
        <Form.Item
          label=""
          wrapperCol={{
            offset: 5,
            span: 16,
          }}
        >
          <h1>商户基本信息</h1>
        </Form.Item>
        <Form.Item label="进件类型" name="operationType" initialValue={operationTypes}>
          <Radio.Group onChange={this.onChange} disabled={Disables}>
            <Radio value={'0'}>个人经营者</Radio>
            <Radio value={'1'}>企业</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="商户名称" name="merchantName" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入商户名称" disabled={Disables} />
        </Form.Item>
        <Form.Item label="联系电话" name="contactPhone" rules={[{ required: Disablesss }]}>
          <Input
            placeholder="请输入商户联系电话"
            disabled={Disables}
            onChange={this.props.contactPhones}
          />
        </Form.Item>
        <Form.Item label="邮箱地址" name="email" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入商户邮箱地址" disabled={Disables} />
        </Form.Item>
        <Form.Item label="门店名称" name="storeName" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入门店名称" disabled={Disables} />
        </Form.Item>

        <Form.Item label="请选择门店所属地区" name="storeCounty" rules={[{ required: Disablesss }]}>
          <Cascader
            disabled={Disables}
            onChange={(e) => {
              this.area(e);
            }}
            options={this.state.bbb}
            placeholder="请选择"
          />
        </Form.Item>

        <Form.Item label="详细地址" name="storeAddr" rules={[{ required: Disablesss }]}>
          <Input
            placeholder="请输入门店详细地址"
            disabled={Disables}
            onChange={this.props.addressa}
          />
        </Form.Item>

        {this.state.operationTypes == 1 ? (
          <>
            <Form.Item
              label=""
              wrapperCol={{
                offset: 5,
                span: 16,
              }}
            >
              <h1>经营主体信息</h1>
            </Form.Item>

            <Form.Item label="主体名称" name="businessName" rules={[{ required: Disablesss }]}>
              <Input
                placeholder="请根据主体营业执照或登记证书准确输入主体名称"
                disabled={Disables}
              />
            </Form.Item>

            {/* <Form.Item label="主体编号" name="parentChannelMerchantNo" rules={[{ required: true }]}>
              <Input placeholder="请根据主体营业执照或登记证书准确输入主体编号"  disabled={Disables} />
            </Form.Item> */}

            <Form.Item
              label="主体类型"
              name="mainType"
              rules={[{ required: Disablesss, message: '请选择主体所属类型' }]}
            >
              <Select
                disabled={Disables}
                allowClear
                showSearch
                optionFilterProp="label"
                placeholder="请选择主体所属类型"
              >
                <Option value={10030}>企业商户</Option>
                <Option value={10031}>个体工商户</Option>
                <Option value={10034}>民办非企业</Option>
                <Option value={10033}>事业单位</Option>
              </Select>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>营业执照照片
                </span>
              }
            >
              <Form.Item noStyle>
                <Upload
                  disabled={Disables}
                  onChange={this.onChangexx}
                  action="/ddql/file/upload"
                  headers={{ token: localStorage.getItem('token') }}
                  listType="picture-card"
                  fileList={licenceUrl}
                  onPreview={this.onPreviewxx}
                >
                  {licenceUrl.length < 1 && '+ 上传'}
                </Upload>
              </Form.Item>
              <div
                style={{
                  padding: '0 10px',
                  boxSizing: 'border-box',
                  border: '1px dashed #d9d9d9',
                  width: 120,
                  textAlign: 'center',
                  marginTop: '-112px',
                  marginLeft: '120px',
                  borderRadius: 3,
                }}
              >
                <img
                  alt="example"
                  style={{
                    width: 100,
                    height: 100,
                    objectFit: 'contain',
                  }}
                  src={require('@/assets/images/yinyezhizhao.svg')}
                />
                <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
              </div>
            </Form.Item>

            <Form.Item label="执照/证书编号" name="businessNo" rules={[{ required: Disablesss }]}>
              <Input placeholder="请输入主体营业执照或登记证书编号" disabled={Disables} />
            </Form.Item>

            <Form.Item label="法人姓名" name="legalPerson" rules={[{ required: Disablesss }]}>
              <Input
                placeholder="请根据证件信息准确输入主体法定代表人姓名"
                disabled={Disables}
                onChange={this.props.fullName}
              />
            </Form.Item>

            <Form.Item label="所属地区" name="businessProvince" rules={[{ required: Disablesss }]}>
              <Cascader options={this.state.bbb} placeholder="请选择" disabled={Disables} />
            </Form.Item>

            <Form.Item label="详细地址" name="businessAddr" rules={[{ required: Disablesss }]}>
              <Input
                placeholder="请根据主体营业执照或登记证书准确输入详细地址"
                disabled={Disables}
              />
            </Form.Item>

            <Form.Item
              label="执照/证书有效期"
              name="effectiveType"
              rules={[{ required: Disablesss }]}
              initialValue={this.state.effectiveType}
            >
              <Radio.Group onChange={this.onChanges} disabled={Disables}>
                <Radio value={'0'}>有效期</Radio>
                <Radio value={'1'}>长期</Radio>
              </Radio.Group>
            </Form.Item>

            {this.state.effectiveType == 0 ? (
              <>
                <Form.Item
                  label=""
                  name="businessBeginDate"
                  rules={[{ required: Disablesss }]}
                  wrapperCol={{
                    offset: 8,
                    span: 16,
                  }}
                >
                  <RangePicker disabled={Disables} />
                </Form.Item>
              </>
            ) : (
              <>
                <Form.Item
                  label="开始时间"
                  name="businessBeginDate"
                  rules={[{ required: Disablesss }]}
                >
                  <DatePicker disabled={Disables} />
                </Form.Item>
              </>
            )}
          </>
        ) : (
          ''
        )}

        <Form.Item
          label=""
          wrapperCol={{
            offset: 5,
            span: 16,
          }}
        >
          <h1>负责人身份证信息</h1>
        </Form.Item>

        <Form.Item
          label={
            <span>
              <span style={{ color: 'red' }}>*</span>身份证人像面照片
            </span>
          }
        >
          <Form.Item noStyle>
            <Upload
              disabled={Disables}
              onChange={this.onChangeA}
              name="file"
              action="/ddql/file/upload"
              listType="picture-card"
              fileList={IdPhoto}
              headers={{ token: localStorage.getItem('token') }}
              // onChange={this.onChangeA}
              onPreview={this.onPreviewA}
            >
              {IdPhoto.length < 1 && '+ 上传'}
            </Upload>
          </Form.Item>

          <div
            style={{
              padding: '0 10px',
              boxSizing: 'border-box',
              border: '1px dashed #d9d9d9',
              width: 120,
              textAlign: 'center',
              marginTop: '-112px',
              marginLeft: '120px',
              borderRadius: 3,
            }}
          >
            <img
              alt="example"
              style={{
                width: 100,
                height: 100,
                objectFit: 'contain',
              }}
              src={require('@/assets/images/cardzhengmian.svg')}
            />
            <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
          </div>
        </Form.Item>

        <Form.Item label="证件持有人姓名" name="cardName" rules={[{ required: Disablesss }]}>
          <Input placeholder="请根据证件信息准确输入证件持有人姓名" disabled={Disables} />
        </Form.Item>
        <Form.Item label="证件号码" name="cardNo" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入身份证号码" disabled={Disables} />
        </Form.Item>
        <Form.Item label="手机号码" name="cardMobile" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入证件持有人手机号码" disabled={Disables} />
        </Form.Item>

        <Form.Item
          label={
            <span>
              <span style={{ color: 'red' }}>*</span>身份证国徽面照片
            </span>
          }
        >
          <Form.Item noStyle>
            <Upload
              disabled={Disables}
              onChange={this.onChangeB}
              name="file"
              action="/ddql/file/upload"
              headers={{ token: localStorage.getItem('token') }}
              listType="picture-card"
              fileList={IdPhotoB}
              // onChange={this.onChangeB}
              onPreview={this.onPreviewB}
            >
              {IdPhotoB.length < 1 && '+ 上传'}
            </Upload>
          </Form.Item>

          <div
            style={{
              padding: '0 10px',
              boxSizing: 'border-box',
              border: '1px dashed #d9d9d9',
              width: 120,
              textAlign: 'center',
              marginTop: '-112px',
              marginLeft: '120px',
              borderRadius: 3,
            }}
          >
            <img
              alt="example"
              style={{
                width: 100,
                height: 100,
                objectFit: 'contain',
              }}
              src={require('@/assets/images/shenfenzhenshilie.svg')}
            />
            <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
          </div>
        </Form.Item>

        <Form.Item
          label="证件有效期"
          name="documentType"
          rules={[{ required: Disablesss }]}
          initialValue={documentType}
        >
          <Radio.Group onChange={this.onChangeg} disabled={Disables}>
            <Radio value={'0'}>有效期</Radio>
            <Radio value={'1'}>长期</Radio>
          </Radio.Group>
        </Form.Item>

        {this.state.documentType == 0 ? (
          <>
            <Form.Item
              label=""
              name="cardBeginDate"
              rules={[{ required: Disablesss }]}
              wrapperCol={{
                offset: 8,
                span: 16,
              }}
            >
              <RangePicker disabled={Disables} />
            </Form.Item>
          </>
        ) : (
          <>
            <Form.Item label="开始时间" name="cardBeginDate" rules={[{ required: Disablesss }]}>
              <DatePicker disabled={Disables} />
            </Form.Item>
          </>
        )}

        <Form.Item
          label=""
          wrapperCol={{
            offset: 5,
            span: 16,
          }}
        >
          <h1>分账账户信息</h1>
        </Form.Item>

        <Form.Item
          label="账户类型"
          name="accType"
          rules={[{ required: Disablesss, message: '请选择账户所属类型' }]}
        >
          <Select
            allowClear
            showSearch
            optionFilterProp="label"
            placeholder="请选择账户所属类型"
            disabled={Disables}
            onChange={(e) => {
              this.setState({ accType: e });
            }}
          >
            <Option value={10070}>对公账户</Option>
            <Option value={10071}>法人账户</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label={
            <span>
              <span style={{ color: 'red' }}>*</span>
              {this.state.accType == 10070 ? '开户许可证照片' : '法人银行卡照片'}
            </span>
          }
        >
          <Form.Item noStyle>
            <Upload
              disabled={Disables}
              onChange={this.onChangev}
              name="file"
              headers={{ token: localStorage.getItem('token') }}
              action="/ddql/file/upload"
              listType="picture-card"
              fileList={openAccountLicenceUrl}
              onPreview={this.onPreviewv}
            >
              {openAccountLicenceUrl.length < 1 && '+ 上传'}
            </Upload>
          </Form.Item>

          <div
            style={{
              padding: '0 10px',
              boxSizing: 'border-box',
              border: '1px dashed #d9d9d9',
              width: 120,
              textAlign: 'center',
              marginTop: '-112px',
              marginLeft: '120px',
              borderRadius: 3,
            }}
          >
            <img
              alt="example"
              style={{
                width: 100,
                height: 100,
                objectFit: 'contain',
              }}
              src={
                this.state.accType == 10071
                  ? require('@/assets/images/yinhangkashili.png')
                  : require('@/assets/images/kaihuxukezheng.png')
              }
            />
            <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
          </div>
        </Form.Item>

        <Form.Item
          label={
            <span>
              <span style={{ color: 'red' }}>*</span>银行账号
            </span>
          }
        >
          <Form.Item name="accCardNo" noStyle>
            <Input placeholder="请输入银行账号" disabled={Disables} />
          </Form.Item>
          <span style={{ color: '#ccc' }}>注意：需严格按照账户类型输入对应的银行帐户</span>
        </Form.Item>

        <Form.Item
          label={
            <span>
              <span style={{ color: 'red' }}>*</span>账户名
            </span>
          }
        >
          <Form.Item name="accName" noStyle>
            <Input placeholder="请输入银行账户名" disabled={Disables} />
          </Form.Item>
          <span style={{ color: '#ccc' }}>根据账户所属类型输入法人姓名或开户许可证主体名称</span>
        </Form.Item>

        <Form.Item label="开户预留手机" name="accMobile" rules={[{ required: Disablesss }]}>
          <Input placeholder="请输入银行账户开户预留手机" disabled={Disables} />
        </Form.Item>

        <Form.Item
          label="请选择开户行"
          name="bank"
          rules={[{ required: Disablesss, message: '请选择开户行' }]}
        >
          <Select
            disabled={Disables}
            allowClear
            showSearch
            optionFilterProp="label"
            placeholder="请选择开户行"
            onChange={this.banks}
          >
            {aaas.map((res) => {
              return (
                <Option value={res.bank} key={res.bank} label={`${res.bank}${res.bankName}`}>
                  {res.bankName}
                </Option>
              );
            })}
          </Select>
        </Form.Item>

        <Form.Item
          label="开户所属地区"
          name="province"
          rules={[{ required: Disablesss, message: '开户所属地区' }]}
        >
          <Cascader
            disabled={Disables}
            onChange={this.AccountOpeningAddress}
            options={this.state.ccc}
            placeholder="请选择"
          />
        </Form.Item>

        <Form.Item
          label="支行"
          name="bankLinkNo"
          rules={[{ required: Disablesss, message: '支行' }]}
        >
          <Select
            disabled={Disables}
            allowClear
            showSearch
            optionFilterProp="label"
            placeholder="支行"
            onChange={this.Subbranch}
          >
            {dd.map((res) => {
              return (
                <Option
                  value={res.bankLinkNo}
                  key={res.bankLinkNo}
                  label={`${res.bankLinkNo}${res.bankLinkName}`}
                >
                  {res.bankLinkName}
                </Option>
              );
            })}
          </Select>
        </Form.Item>

        <Modal
          visible={this.state.previewOpen}
          title={'预览'}
          footer={null}
          width={800}
          onCancel={this.handleCancel}
        >
          <img
            alt="example"
            style={{
              width: '100%',
            }}
            src={this.state.imgWindows}
          />
        </Modal>
      </>
    );
  }
}

export default connect()(App);
