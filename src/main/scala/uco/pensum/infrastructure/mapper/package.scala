package uco.pensum.infrastructure

import uco.pensum.infrastructure.http.dtos.mapper.{
  MapperDTOInstances,
  MapperRecordsInstances,
  MapperReportInstances
}

package object mapper {
  object MapperRecords extends MapperRecordsInstances
  object MapperProductDTO extends MapperDTOInstances
  object MapperReports extends MapperReportInstances
}
