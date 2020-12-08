package io.erva.lund.data.parser

interface Parser<SOURCE> {

  fun parse(source: SOURCE): Transaction?
}