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
  Switch,
  Modal,
  Upload,
  Cascader,
} from 'antd';
import yinyezhizhaologo from '@/assets/images/yinyezhizhao.svg';
import cardzhengmian from '@/assets/images/cardzhengmian.svg';
import shenfenzhenshilie from '@/assets/images/shenfenzhenshilie.svg';
import { PageContainer } from '@ant-design/pro-layout';
import { history, Link } from 'umi';
import XX from './components/xx';
import moment from 'moment';
import { ZoomInOutlined } from '@ant-design/icons';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import { BankingDetails } from '@/utils/utils';
import dayjs from 'dayjs';
// import { setToken } from '@/utils/authority';
const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 10 },
};

const { Option } = Select;
const { RangePicker } = DatePicker;

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    xx: { merchantBasic: {} },
  };

  componentDidMount() {
    this.getData();

    const aaa = [];
    const bbb = [];
    BankingDetails.map((res) => {
      if (!aaa.some((resd) => resd.provinceName == res.provinceName)) {
        aaa.push(res);
      }
    });
    aaa.map((resd) => {
      const aa = BankingDetails.filter((res) => res.provinceName == resd.provinceName);
      const asdasdsa = [];
      aa.map((res) => {
        if (!asdasdsa.some((resd) => resd.marketName == res.marketName)) {
          asdasdsa.push({
            marketName: res.marketName,
            city: res.city,
            label: res.marketName,
            value: res.city,
            key: res.city,
          });
        }
      });
      resd.children = asdasdsa;
    });
    aaa.map((resd) => {
      resd.key = resd.province;
      resd.value = resd.province;
      resd.label = resd.provinceName;
    });

    const aaas = [];
    BankingDetails.map((res) => {
      if (!aaas.some((resd) => resd.bank == res.bank)) {
        aaas.push(res);
      }
    });

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
    this.setState(
      {
        ccc: aaa,
        bbb: bbb,
        aaas: aaas, //银行列表
      },
      () => {},
    );
  }

  getData = () => {
    console.log(this.props)
    // const { stadium_id, detail, oo } = this.props.location.query;
           const searchParams = new URLSearchParams(history.location?.search || '');
            const detail = searchParams.get('detail');

    this.setState(
      {
        xx: JSON.parse(detail),
      },
      () => {
        const { xx } = this.state;

        this.setState({
          accType: xx.accType ? Number(xx.accType) : undefined, //账户类型（操作类型为1必填）：10070=对公账户，10071=法人账户
          operationTypes: xx.operationType,
          effectiveType: xx.operationType == 1 && (xx.businessEndDate == '长期' ? 1 : 0),
          licenceUrl: [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.licenceUrlLocal,
            },
          ],

          licenceUrls: [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.licenceUrlLocal,
            },
          ],

          IdPhoto: xx.legalLicenceFrontUrl
            ? [
                {
                  uid: '-1',
                  name: 'image.png',
                  status: 'done',
                  url: xx.legalLicenceFrontUrl,
                },
              ]
            : [], //身份证正面

          IdPhotoB: xx.legalLicenceBackUrl
            ? [
                {
                  uid: '-1',
                  name: 'image.png',
                  status: 'done',
                  url: xx.legalLicenceBackUrl,
                },
              ]
            : [], //身份证反面

          documentType: xx.cardEndDate == '长期' ? 1 : 0,

          openAccountLicenceUrl: xx.openAccountLicenceUrl
            ? [
                {
                  uid: '-1',
                  name: 'image.png',
                  status: 'done',
                  url: xx.openAccountLicenceUrl,
                },
              ]
            : [], //*开户许可证照片
        });
        this.formRef.current.setFieldsValue({
          // stadium_id: Number(stadium_id),
          operationType: Number(xx.operationType),
          merchantName: xx.merchantName, //商户名称
          contactPhone: xx.contactPhone, //联系电话
          email: xx.email, //邮箱地址

          storeName: xx.storeName, //门店名称
          storeCounty: xx.storeProvince
            ? [xx.storeProvince, xx.storeCity, xx.storeCounty]
            : undefined, //门店所属地区
          storeAddr: xx.storeAddr, //详情地址

          businessName: xx.operationType == 1 && xx.businessName, //主体名称
          businessNo: xx.operationType == 1 && xx.businessNo,
          mainType: xx.mainType ? xx.operationType == 1 && Number(xx.mainType) : undefined, //主体类型
          licenceUrl: xx.licenceUrl, //营业执照照片
          parentChannelMerchantNo: xx.parentChannelMerchantNo, //执照/证书编号
          legalPerson: xx.operationType == 1 && xx.legalPerson, //法人姓名

          businessProvince:
            xx.operationType == 1 &&
            (xx.businessProvince
              ? [xx.businessProvince, xx.businessCity, xx.businessCounty]
              : undefined),
          businessAddr: xx.operationType == 1 && xx.businessAddr,
          effectiveType: xx.operationType == 1 && (xx.businessEndDate == '长期' ? 1 : 0),
          businessBeginDate: xx.businessEndDate
            ? xx.operationType == 1 &&
              (xx.businessEndDate == '长期'
                ? dayjs(xx.businessBeginDate, 'YYYY-MM-DD')
                : [
                    dayjs(xx.businessBeginDate, 'YYYY-MM-DD'),
                    dayjs(xx.businessEndDate, 'YYYY-MM-DD'),
                  ])
            : undefined,

          documentType: xx.cardEndDate == '长期' ? 1 : 0,
          cardBeginDate: xx.cardEndDate
            ? xx.cardEndDate == '长期'
              ? dayjs(xx.cardBeginDate, 'YYYY-MM-DD')
              : [dayjs(xx.cardBeginDate, 'YYYY-MM-DD'), dayjs(xx.cardEndDate, 'YYYY-MM-DD')]
            : undefined,

          cardName: xx.cardName, //证件持有人姓名
          cardNo: xx.cardNo, //证件号码
          cardMobile: xx.cardMobile, //手机号码

          accType: xx.accType ? Number(xx.accType) : undefined, //账户类型（操作类型为1必填）：10070=对公账户，10071=法人账户
          accCardNo: xx.accCardNo, //银行账号
          accMobile: xx.accMobile, //银行开户预留手机号
          accName: xx.accName, //户名（法人或营业执照名称）

          bank: xx.bank,
          province: xx.province ? [xx.province, xx.city] : undefined,
          bankBranch: xx.bankBranch,
        });
      },
    );
    const { xx = {} } = this.state;

    // const xx = JSON.parse(detail);
  };

  //营业执照照片

  // onChangexx = (e) => {
  //   console.log(e);
  //   this.setState({
  //     licenceUrl: e.fileList,
  //   });
  //   const { response = {} } = e.file;
  //   if (e.file.status == 'done') {
  //     const reader = new FileReader();
  //     reader.readAsDataURL(e.file.originFileObj);
  //     reader.onload = () => {
  //       this.props.dispatch({
  //         type: 'myModel/getSetData',
  //         payload: {
  //           fileStr: reader.result,
  //         },
  //         url: `/guzhe/merchant/image/upload`,
  //         method: 'POST',
  //         myData: (res) => {
  //           if (res && res.code == 10000) {
  //             this.setState(
  //               {
  //                 licenceUrl: [
  //                   {
  //                     uid: '-1',
  //                     name: 'image.png',
  //                     status: 'done',
  //                     url: res.data,
  //                   },
  //                 ],
  //               },
  //               () => {
  //                 this.props.IDImage(
  //                   this.state.IdPhoto,
  //                   this.state.IdPhotoB,
  //                   this.state.licenceUrl,
  //                   this.state.openAccountLicenceUrl,
  //                 );
  //               },
  //             );
  //           } else {
  //             message.error(res.message);
  //             // this.setState({ isSelectForm: true });
  //           }
  //         },
  //       });
  //     };
  //   }
  // };

  onPreview = async (file) => {
    let src = file.url;
    if (!src) {
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

  render() {
    const {
      placeList = [],
      fileList = [],
      operationTypes,
      licenceUrl = [],
      IdPhoto = [],
      IdPhotoB = [],
      documentType,
      openAccountLicenceUrl = [],
      aaas = [],
    } = this.state;
    const routes = [
      {
        path: '/',
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '商户管理',
      },
      {
        breadcrumbName: `查看商户`,
      },
    ];
    return (
      <PageContainer
        header={{
          title: (
            <div style={{ display: 'flex', gap: 10 }}>
              {this.state.xx.merchantName}-商户详情
              {this.state?.xx?.applicationStatus == 'REVIEW_BACK' && (
                <div style={{ color: 'red' }}>驳回原因：{this.state?.xx?.auditOpinion}</div>
              )}
            </div>
          ),
          breadcrumb: {
            routes,
          },
          extra: (
            <div>
              <Button onClick={() => history.back()}>返回</Button>
            </div>
          ),
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', paddingTop: 50, paddingBottom: 50 }}>
            <Form ref={this.formRef} {...layout}>
              {/* <Form.Item
                label="选择场所"
                name="stadium_id"
                rules={[{ required: true, message: '请选择' }]}
              >
                <Select
                  
                  allowClear
                  showSearch
                  optionFilterProp="label"
                  placeholder="请选择需要开通商户功能的场所"
                >
                  {placeList.map((res) => {
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
                <Radio.Group onChange={this.onChange}>
                  <Radio value={0}>个人经营者</Radio>
                  <Radio value={1}>企业</Radio>
                </Radio.Group>
              </Form.Item>
              <Form.Item label="商户名称" name="merchantName" rules={[{ required: true }]}>
                <Input placeholder="请输入商户名称" />
              </Form.Item>
              <Form.Item label="联系电话" name="contactPhone" rules={[{ required: true }]}>
                <Input placeholder="请输入商户联系电话" />
              </Form.Item>
              <Form.Item label="邮箱地址" name="email" rules={[{ required: true }]}>
                <Input placeholder="请输入商户邮箱地址" />
              </Form.Item>
              <Form.Item label="门店名称" name="storeName" rules={[{ required: true }]}>
                <Input placeholder="请输入门店名称" />
              </Form.Item>

              <Form.Item label="请选择门店所属地区" name="storeCounty" rules={[{ required: true }]}>
                <Cascader
                  // onChange={(e) => {this.area(e)}}
                  options={this.state.bbb}
                  placeholder="请选择"
                />
              </Form.Item>

              <Form.Item label="详细地址" name="storeAddr" rules={[{ required: true }]}>
                <Input placeholder="请输入门店详细地址" />
              </Form.Item>

              {operationTypes == 1 ? (
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

                  <Form.Item label="主体名称" name="businessName" rules={[{ required: true }]}>
                    <Input placeholder="请根据主体营业执照或登记证书准确输入主体名称" />
                  </Form.Item>

                  <Form.Item
                    label="主体类型"
                    name="mainType"
                    rules={[{ required: true, message: '请选择主体所属类型' }]}
                  >
                    <Select
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
                      {/* <ImgCrop {...props}> */}
                      <Upload
                        action="/guzhe/file/upload"
                        // headers={{ token: localStorage.getItem('token') }}
                        listType="picture-card"
                        fileList={licenceUrl}
                        onChange={this.onChangexx}
                        onPreview={this.onPreview}
                      >
                        {licenceUrl.length < 1 && '+ 上传'}
                      </Upload>
                      {/* </ImgCrop> */}
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
                        minHeight: 100,
                      }}
                    >
                      <img
                        alt="example"
                        style={{
                          width: 100,
                          height: 100,
                          objectFit: 'contain',
                        }}
                        src={yinyezhizhaologo}
                      />
                      <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
                    </div>
                  </Form.Item>

                  <Form.Item label="执照/证书编号" name="businessNo" rules={[{ required: true }]}>
                    <Input placeholder="请输入主体营业执照或登记证书编号" />
                  </Form.Item>

                  <Form.Item label="法人姓名" name="legalPerson" rules={[{ required: true }]}>
                    <Input placeholder="请根据证件信息准确输入主体法定代表人姓名" />
                  </Form.Item>

                  <Form.Item label="所属地区" name="businessProvince" rules={[{ required: true }]}>
                    <Cascader options={this.state.bbb} placeholder="请选择" />
                  </Form.Item>

                  <Form.Item label="详细地址" name="businessAddr" rules={[{ required: true }]}>
                    <Input placeholder="请根据主体营业执照或登记证书准确输入详细地址" />
                  </Form.Item>

                  <Form.Item
                    label="执照/证书有效期"
                    name="effectiveType"
                    rules={[{ required: true }]}
                  >
                    <Radio.Group onChange={this.onChanges} initialValue={this.state.effectiveType}>
                      <Radio value={0}>有效期</Radio>
                      <Radio value={1}>长期</Radio>
                    </Radio.Group>
                  </Form.Item>

                  {this.state.effectiveType == 0 ? (
                    <>
                      <Form.Item
                        label=""
                        name="businessBeginDate"
                        rules={[{ required: true }]}
                        wrapperCol={{
                          offset: 8,
                          span: 16,
                        }}
                      >
                        <RangePicker />
                      </Form.Item>
                    </>
                  ) : (
                    <>
                      <Form.Item
                        label="开始时间"
                        name="businessBeginDate"
                        rules={[{ required: true }]}
                      >
                        <RangePicker />
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
                    action="/guzhe/file/upload"
                    //  headers={{ token: localStorage.getItem('token') }}
                    listType="picture-card"
                    fileList={IdPhoto}
                    onChange={this.onChangeA}
                    onPreview={this.onPreview}
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
                    minHeight: 100,
                  }}
                >
                  <img
                    alt="example"
                    style={{
                      width: 100,
                      height: 100,
                      objectFit: 'contain',
                    }}
                    src={cardzhengmian}
                  />
                  <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
                </div>
              </Form.Item>

              <Form.Item label="证件持有人姓名" name="cardName" rules={[{ required: true }]}>
                <Input placeholder="请根据证件信息准确输入证件持有人姓名" />
              </Form.Item>
              <Form.Item label="证件号码" name="cardNo" rules={[{ required: true }]}>
                <Input placeholder="请输入身份证号码" />
              </Form.Item>
              <Form.Item label="手机号码" name="cardMobile" rules={[{ required: true }]}>
                <Input placeholder="请输入证件持有人手机号码" />
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
                    action="/guzhe/file/upload"
                    //  headers={{ token: localStorage.getItem('token') }}
                    listType="picture-card"
                    fileList={IdPhotoB}
                    onChange={this.onChangeB}
                    onPreview={this.onPreview}
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
                    minHeight: 100,
                  }}
                >
                  <img
                    alt="example"
                    style={{
                      width: 100,
                      height: 100,
                      objectFit: 'contain',
                    }}
                    src={shenfenzhenshilie}
                  />
                  <div style={{ marginTop: '-18px', color: '#ccc' }}>示例图</div>
                </div>
              </Form.Item>

              <Form.Item label="证件有效期" name="documentType" rules={[{ required: true }]}>
                <Radio.Group onChange={this.onChangeg}>
                  <Radio value={0}>有效期</Radio>
                  <Radio value={1}>长期</Radio>
                </Radio.Group>
              </Form.Item>

              {this.state.documentType == 0 ? (
                <>
                  <Form.Item
                    label=""
                    name="cardBeginDate"
                    rules={[{ required: true }]}
                    wrapperCol={{
                      offset: 8,
                      span: 16,
                    }}
                  >
                    <RangePicker />
                  </Form.Item>
                </>
              ) : (
                <>
                  <Form.Item label="开始时间" name="cardBeginDate" rules={[{ required: true }]}>
                    <RangePicker />
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
                rules={[{ required: true, message: '请选择账户所属类型' }]}
              >
                <Select
                  allowClear
                  showSearch
                  optionFilterProp="label"
                  placeholder="请选择账户所属类型"
                  onChange={(e) => {
                    this.setState({
                      accType: e,
                    });
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
                    action="/guzhe/file/upload"
                    //  headers={{ token: localStorage.getItem('token') }}
                    listType="picture-card"
                    fileList={openAccountLicenceUrl}
                    onChange={this.onChangev}
                    onPreview={this.onPreview}
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
                    minHeight: 100,
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
                  <Input placeholder="请输入银行账号" />
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
                  <Input placeholder="请输入银行账户名" />
                </Form.Item>
                <span style={{ color: '#ccc' }}>
                  根据账户所属类型输入法人姓名或开户许可证主体名称
                </span>
              </Form.Item>

              <Form.Item label="开户预留手机" name="accMobile" rules={[{ required: true }]}>
                <Input placeholder="请输入银行账户开户预留手机" />
              </Form.Item>

              <Form.Item
                label="请选择开户行"
                name="bank"
                rules={[{ required: true, message: '请选择开户行' }]}
              >
                <Select allowClear showSearch optionFilterProp="label" placeholder="请选择开户行">
                  {aaas.map((res) => {
                    return <Option value={res.bank}>{res.bankName}</Option>;
                  })}
                </Select>
              </Form.Item>

              <Form.Item
                label="开户所属地区"
                name="province"
                rules={[{ required: true, message: '开户所属地区' }]}
              >
                <Cascader
                  onChange={(e) => {
                    const aa = BankingDetails.filter((res) => res.city == e[1]);
                    this.setState(
                      {
                        dd: aa,
                      },
                      () => {},
                    );
                  }}
                  options={this.state.ccc}
                  placeholder="请选择"
                />
              </Form.Item>

              <Form.Item
                label="支行"
                name="bankBranch"
                rules={[{ required: true, message: '支行' }]}
              >
                <Input placeholder="请输入" />
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
            </Form>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default NoticeNotice;
