package com.pagatodoholdings.posandroid.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher {

    private final String mask;
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private boolean isScanned = false;

    public MaskWatcher(String mask) {
        this.mask = mask;
    }

    public static MaskWatcher phoneFormat() {
        return new MaskWatcher("## #### ####");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        isDeleting = count > after;
        isScanned = after - start > 1;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isRunning || isDeleting) {
            return;
        }
        isRunning = true;

        if (!editable.toString().isEmpty()) {
            if (isScanned) {
                String tempEditable = editable.toString();
                if (!mask.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    editable.clear();
                    int j = 0;
                    for (int i = 0; i < mask.length(); i++) {
                        char mChar = mask.charAt(i);
                        if (Character.toString(mChar).equals("#")) {
                            if (i < tempEditable.length() + j) {
                                stringBuilder.append(tempEditable.charAt(i - j));
                            } else {
                                break;
                            }
                        } else {
                            stringBuilder.append(" ");
                            j++;
                        }
                    }
                    editable.append(stringBuilder);
                }
            } else {
                int editableLength = editable.length();
                if (editableLength < mask.length()) {
                    if (mask.charAt(editableLength) != '#') {
                        editable.append(mask.charAt(editableLength));
                    } else if (mask.charAt(editableLength - 1) != '#') {
                        editable.insert(editableLength - 1, mask, editableLength - 1, editableLength);
                    }
                }
            }
        }

        if(editable.toString().length()>mask.length())editable.delete(mask.length(),mask.length()+1);

        isRunning = false;
    }
}