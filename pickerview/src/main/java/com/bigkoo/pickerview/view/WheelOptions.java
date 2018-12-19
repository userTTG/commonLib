package com.bigkoo.pickerview.view;

import android.graphics.Typeface;
import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.List;

public class WheelOptions<T> {
    private View view;
    private WheelView wv_option1;
    private WheelView wv_option2;
    private WheelView wv_option3;
    private WheelView wv_option4;

    private List<T> mOptions1Items;
    private List<List<T>> mOptions2Items;
    private List<List<List<T>>> mOptions3Items;
    private List<List<List<List<T>>>> mOptions4Items;

    private boolean linkage = true;//默认联动
    private boolean isRestoreItem; //切换时，还原第一项
    private OnItemSelectedListener wheelListener_option1;
    private OnItemSelectedListener wheelListener_option2;
    private OnItemSelectedListener wheelListener_option3;

    private OnOptionsSelectChangeListener optionsSelectChangeListener;

    //文字的颜色和分割线的颜色
    private int textColorOut;
    private int textColorCenter;
    private int dividerColor;

    private WheelView.DividerType dividerType;

    // 条目间距倍数
    private float lineSpacingMultiplier;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelOptions(View view, boolean isRestoreItem) {
        super();
        this.isRestoreItem = isRestoreItem;
        this.view = view;
        wv_option1 = (WheelView) view.findViewById(R.id.options1);// 初始化时显示的数据
        wv_option2 = (WheelView) view.findViewById(R.id.options2);
        wv_option3 = (WheelView) view.findViewById(R.id.options3);
        wv_option4 = (WheelView) view.findViewById(R.id.options4);
    }

    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items){
        setPicker(options1Items,options2Items,options3Items,null);
    }


    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items,List<List<List<List<T>>>> options4Items) {
        this.mOptions1Items = options1Items;
        this.mOptions2Items = options2Items;
        this.mOptions3Items = options3Items;
        this.mOptions4Items = options4Items;

        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter(mOptions1Items));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        // 选项2
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(0)));// 设置显示数据
        }
        wv_option2.setCurrentItem(wv_option2.getCurrentItem());// 初始化时显示的数据
        // 选项3
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(0).get(0)));// 设置显示数据
        }
        // 选项4
        if (mOptions4Items != null) {
            wv_option4.setAdapter(new ArrayWheelAdapter(mOptions4Items.get(0).get(0).get(0)));// 设置显示数据
        }
        if (mOptions4Items == null)
            wv_option3.setCurrentItem(wv_option3.getCurrentItem());
        else {
            wv_option4.setCurrentItem(wv_option4.getCurrentItem());
        }
        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
        wv_option3.setIsOptions(true);
        if (wv_option4!=null)
            wv_option4.setIsOptions(true);

        if (this.mOptions2Items == null) {
            wv_option2.setVisibility(View.GONE);
        } else {
            wv_option2.setVisibility(View.VISIBLE);
        }
        if (this.mOptions3Items == null) {
            wv_option3.setVisibility(View.GONE);
        } else {
            wv_option3.setVisibility(View.VISIBLE);
        }
        if (wv_option4!=null)
            wv_option4.setVisibility(this.mOptions4Items == null?View.GONE:View.VISIBLE);

        // 联动监听器
        wheelListener_option1 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
                int opt2Select = 0;
                if (mOptions2Items == null) {//只有1级联动数据
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), 0, 0, 0);
                    }
                } else {
                    if (!isRestoreItem) {
                        //上一个opt2的选中位置
                        //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt2Select = Math.min(wv_option2.getCurrentItem(),mOptions2Items.get(index).size() - 1);
                    }
                    wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(index)));
                    wv_option2.setCurrentItem(opt2Select);

                    if (mOptions3Items != null) {
                        wheelListener_option2.onItemSelected(opt2Select);
                    } else {//只有2级联动数据，滑动第1项回调
                        if (optionsSelectChangeListener != null) {
                            optionsSelectChangeListener.onOptionsSelectChanged(index, opt2Select, 0 , 0);
                        }
                    }
                }
            }
        };

        wheelListener_option2 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
                if(mOptions3Items == null) {//只有2级联动数据，滑动第2项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, 0,0);
                    }
                }else {
                    int opt1Select = Math.min(mOptions3Items.size() - 1,wv_option1.getCurrentItem());
                    int opt2Select = Math.min(mOptions3Items.get(opt1Select).size() - 1,wv_option2.getCurrentItem());
                    int opt3 = 0;
                    if (!isRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt3 = Math.min(wv_option3.getCurrentItem(),mOptions3Items.get(opt1Select).get(opt2Select).size() - 1);
                    }
                    wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(wv_option1.getCurrentItem()).get(opt2Select)));
                    wv_option3.setCurrentItem(opt3);

                    if (mOptions4Items !=null){
                        wheelListener_option3.onItemSelected(opt3);
                    }else {
                        //3级联动数据实时回调
                        if (optionsSelectChangeListener != null) {
                            optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, opt3,0);
                        }
                    }

                }
            }
        };

        wheelListener_option3 = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (mOptions4Items != null) {
                    int opt1Select = wv_option1.getCurrentItem();
                    int opt2Select = wv_option2.getCurrentItem();
                    int opt3Select = wv_option3.getCurrentItem();
                    opt1Select = opt1Select >= mOptions4Items.size() - 1 ? mOptions4Items.size() - 1 : opt1Select;
                    opt2Select = Math.min(opt2Select,mOptions4Items.get(opt1Select).size()-1);
                    opt3Select = Math.min(opt3Select,mOptions4Items.get(opt1Select).get(opt2Select).size()-1);
                    int opt4Select = 0;
                    if (!isRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt4Select = Math.min(wv_option4.getCurrentItem(),mOptions4Items.get(opt1Select).get(opt2Select).get(opt3Select).size() - 1);
                    }
                    wv_option4.setAdapter(new ArrayWheelAdapter(mOptions4Items.get(opt1Select).get(opt2Select).get(opt3Select)));
                    wv_option4.setCurrentItem(opt4Select);


                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(),index, opt4Select);
                    }


                } else {//只有3级联动数据，滑动第3项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), index,0);
                    }
                }
            }
        };

        // 添加联动监听
        if (options1Items != null && linkage) {
            wv_option1.setOnItemSelectedListener(wheelListener_option1);
        }
        if (options2Items != null && linkage) {
            wv_option2.setOnItemSelectedListener(wheelListener_option2);
        }
        if (options3Items != null && linkage) {
            wv_option3.setOnItemSelectedListener(wheelListener_option3);
        }

        //用户设置的监听
        if (options3Items != null && linkage && optionsSelectChangeListener != null && options4Items==null) {
            wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), index,0);
                }
            });
        }
        if (options4Items != null && linkage && optionsSelectChangeListener != null) {
            wv_option4.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), wv_option3.getCurrentItem(),index);
                }
            });
        }
    }


    //不联动情况下
    public void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items,List<T> options4Items) {

        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter(options1Items));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        // 选项2
        if (options2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(options2Items));// 设置显示数据
        }
        wv_option2.setCurrentItem(wv_option2.getCurrentItem());// 初始化时显示的数据
        // 选项3
        if (options3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(options3Items));// 设置显示数据
        }
        wv_option3.setCurrentItem(wv_option3.getCurrentItem());
        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
        wv_option3.setIsOptions(true);

        if (optionsSelectChangeListener != null) {
            wv_option1.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    optionsSelectChangeListener.onOptionsSelectChanged(index, wv_option2.getCurrentItem(), wv_option3.getCurrentItem(),wv_option3!=null?wv_option3.getCurrentItem():0);
                }
            });
        }

        if (options2Items == null) {
            wv_option2.setVisibility(View.GONE);
        } else {
            wv_option2.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option2.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, wv_option3.getCurrentItem(),wv_option3!=null?wv_option3.getCurrentItem():0);
                    }
                });
            }
        }
        if (options3Items == null) {
            wv_option3.setVisibility(View.GONE);
        } else {
            wv_option3.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), index,wv_option3!=null?wv_option3.getCurrentItem():0);
                    }
                });
            }
        }
        if (options4Items == null) {
            wv_option4.setVisibility(View.GONE);
        } else {
            wv_option4.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option4.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), wv_option3.getCurrentItem(),index);
                    }
                });
            }
        }
    }

    public void setTextContentSize(int textSize) {
        wv_option1.setTextSize(textSize);
        wv_option2.setTextSize(textSize);
        wv_option3.setTextSize(textSize);
        if (wv_option4 !=null)
            wv_option4.setTextSize(textSize);
    }

    private void setTextColorOut() {
        wv_option1.setTextColorOut(textColorOut);
        wv_option2.setTextColorOut(textColorOut);
        wv_option3.setTextColorOut(textColorOut);
        if (wv_option4 !=null)
            wv_option4.setTextColorOut(textColorOut);
    }

    private void setTextColorCenter() {
        wv_option1.setTextColorCenter(textColorCenter);
        wv_option2.setTextColorCenter(textColorCenter);
        wv_option3.setTextColorCenter(textColorCenter);
        if (wv_option4 !=null)
            wv_option4.setTextColorCenter(textColorCenter);
    }

    private void setDividerColor() {
        wv_option1.setDividerColor(dividerColor);
        wv_option2.setDividerColor(dividerColor);
        wv_option3.setDividerColor(dividerColor);
        if (wv_option4 !=null)
            wv_option4.setDividerColor(dividerColor);
    }

    private void setDividerType() {
        wv_option1.setDividerType(dividerType);
        wv_option2.setDividerType(dividerType);
        wv_option3.setDividerType(dividerType);
        if (wv_option4 !=null)
            wv_option4.setDividerType(dividerType);
    }

    private void setLineSpacingMultiplier() {
        wv_option1.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_option2.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_option3.setLineSpacingMultiplier(lineSpacingMultiplier);
        if (wv_option4!=null)
            wv_option4.setLineSpacingMultiplier(lineSpacingMultiplier);

    }

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3) {
        setLabels(label1,label2,label3,null);
    }

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3 ,String label4) {
        if (label1 != null) {
            wv_option1.setLabel(label1);
        }
        if (label2 != null) {
            wv_option2.setLabel(label2);
        }
        if (label3 != null) {
            wv_option3.setLabel(label3);
        }
        if (label4 != null && wv_option3 != null) {
            wv_option3.setLabel(label3);
        }
    }

    /**
     * 设置x轴偏移量
     */
    public void setTextXOffset(int x_offset_one, int x_offset_two, int x_offset_three ,int x_offset_fore) {
        wv_option1.setTextXOffset(x_offset_one);
        wv_option2.setTextXOffset(x_offset_two);
        wv_option3.setTextXOffset(x_offset_three);
        if (wv_option4!=null)
            wv_option4.setTextXOffset(x_offset_fore);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    public void setCyclic(boolean cyclic) {
        wv_option1.setCyclic(cyclic);
        wv_option2.setCyclic(cyclic);
        wv_option3.setCyclic(cyclic);
        if (wv_option4!=null)
            wv_option4.setCyclic(cyclic);
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    public void setTypeface(Typeface font) {
        wv_option1.setTypeface(font);
        wv_option2.setTypeface(font);
        wv_option3.setTypeface(font);
        if (wv_option4!=null)
            wv_option4.setTypeface(font);
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3 ,boolean cyclic4) {
        wv_option1.setCyclic(cyclic1);
        wv_option2.setCyclic(cyclic2);
        wv_option3.setCyclic(cyclic3);
        if (wv_option4!=null)
            wv_option4.setCyclic(cyclic4);
    }


    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2。
     * 在快速滑动未停止时，点击确定按钮，会进行判断，如果匹配数据越界，则设为0，防止index出错导致崩溃。
     *
     * @return 索引数组
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[4];
        currentItems[0] = wv_option1.getCurrentItem();

        if (mOptions2Items != null && mOptions2Items.size() > 0) {//非空判断
            currentItems[1] = wv_option2.getCurrentItem() > (mOptions2Items.get(currentItems[0]).size() - 1) ? 0 : wv_option2.getCurrentItem();
        } else {
            currentItems[1] = wv_option2.getCurrentItem();
        }

        if (mOptions3Items != null && mOptions3Items.size() > 0) {//非空判断
            currentItems[2] = wv_option3.getCurrentItem() > (mOptions3Items.get(currentItems[0]).get(currentItems[1]).size() - 1) ? 0 : wv_option3.getCurrentItem();
        } else {
            currentItems[2] = wv_option3.getCurrentItem();
        }

        if (mOptions4Items != null && mOptions4Items.size() > 0) {//非空判断
            currentItems[3] = wv_option4.getCurrentItem() > (mOptions4Items.get(currentItems[0]).get(currentItems[1]).get(currentItems[2]).size() - 1) ? 0 : wv_option4.getCurrentItem();
        } else {
            currentItems[3] = wv_option4.getCurrentItem();
        }

        return currentItems;
    }

    public void setCurrentItems(int option1, int option2, int option3) {
        setCurrentItems(option1,option2,option3,0);
    }

    public void setCurrentItems(int option1, int option2, int option3 ,int option4) {
        if (linkage) {
            itemSelected(option1, option2, option3,option4);
        } else {
            wv_option1.setCurrentItem(option1);
            wv_option2.setCurrentItem(option2);
            wv_option3.setCurrentItem(option3);
            if (wv_option4!=null)
                wv_option4.setCurrentItem(option4);
        }
    }

    private void itemSelected(int opt1Select, int opt2Select, int opt3Select,int op4Select) {
        if (mOptions1Items != null) {
            wv_option1.setCurrentItem(opt1Select);
        }
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(opt1Select)));
            wv_option2.setCurrentItem(opt2Select);
        }
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(opt1Select).get(opt2Select)));
            wv_option3.setCurrentItem(opt3Select);
        }
        if (mOptions4Items != null) {
            wv_option4.setAdapter(new ArrayWheelAdapter(mOptions4Items.get(opt1Select).get(opt2Select).get(opt3Select)));
            wv_option4.setCurrentItem(op4Select);
        }
    }

    /**
     * 设置间距倍数,但是只能在1.2-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        setLineSpacingMultiplier();
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        setDividerColor();
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        this.dividerType = dividerType;
        setDividerType();
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    public void setTextColorCenter(int textColorCenter) {
        this.textColorCenter = textColorCenter;
        setTextColorCenter();
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    public void setTextColorOut(int textColorOut) {
        this.textColorOut = textColorOut;
        setTextColorOut();
    }

    /**
     * Label 是否只显示中间选中项的
     *
     * @param isCenterLabel
     */

    public void isCenterLabel(boolean isCenterLabel) {
        wv_option1.isCenterLabel(isCenterLabel);
        wv_option2.isCenterLabel(isCenterLabel);
        wv_option3.isCenterLabel(isCenterLabel);
        if (wv_option4!=null)
            wv_option4.isCenterLabel(isCenterLabel);
    }

    public void setOptionsSelectChangeListener(OnOptionsSelectChangeListener optionsSelectChangeListener) {
        this.optionsSelectChangeListener = optionsSelectChangeListener;
    }

    public void setLinkage(boolean linkage) {
        this.linkage = linkage;
    }
}
