package com.marvsystems.fotosoftapp.utils

/**
 * @author Anshul Gour on 19,June,2019
Description : -
 */
enum class DateTimeFormat {

    DD_MM_YYYY_WITH_HYPHEN {
        override fun toString(): String {
            return "dd-MM-yyyy"
        }
    },
    YYYY_DD_MM_WITH_HYPHEN {
        override fun toString(): String {
            return "yyyy-MM-dd"
        }
    },

    DD_MM_YYYY_WITH_SLASH {
        override fun toString(): String {
            return "dd/MM/yyyy"
        }
    },
    YYYY_DD_MM_WITH_SLASH {
        override fun toString(): String {
            return "yyyy/MM/dd"
        }
    },
    HH_MM_A {
        override fun toString(): String {
            return "hh:mm a"
        }
    },
    DD_MM_YYYY_WITH_HYPHEN_HH_MM_A {
        override fun toString(): String {
            return "dd-MM-yyyy hh:mm a"
        }
    },
    YYYY_MM_DD_WITH_HYPHEN_HH_MM_A {
        override fun toString(): String {
            return "yyyy-MM-dd hh:mm a"
        }
    },

    DD_MM_YYYY_WITH_SLASH_HH_MM_A {
        override fun toString(): String {
            return "dd/MM/yyyy hh:mm a"
        }
    },
    YYYY_MM_DD_WITH_SLASH_HH_MM_A {
        override fun toString(): String {
            return "yyyy/MM/dd hh:mm a"
        }
    },

    YYYY_MM_DD_T_HH_MM_SS_SSS_Z {
        override fun toString(): String {
            return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        }
    }
}