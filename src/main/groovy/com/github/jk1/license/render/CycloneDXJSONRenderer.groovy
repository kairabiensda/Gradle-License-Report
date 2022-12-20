/*
 * Copyright 2022 Kai Rabien <kai.rabien@sda.se>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jk1.license.render

import com.github.jk1.license.ProjectData
import groovy.json.JsonBuilder
import static com.github.jk1.license.render.LicenseDataCollector.singleModuleLicenseInfo

/**
 *
 * This renderer generates a Software Bill Of Materials (SBOM) in CycloneDX JSON format.
 * See https://cyclonedx.org for more information and specifications.
 *
 * @see com.github.jk1.license.render.JsonReportRenderer
 */
class CycloneDXJSONRenderer implements ReportRenderer {

@Override
void render(ProjectData data) {
  def config = data.project.licenseReport
  def output = new File(config.outputDir, 'bom.json')

  def jsonReport = [
      '$schema': "https://cyclonedx.org/schema/bom-1.4.schema.json",
      "bomFormat": "CycloneDX",
      "specVersion": "1.4",
      "version": 1,
      "components": [],
  ]

  data.allDependencies.forEach( dependency -> {

    def licenseInfo = singleModuleLicenseInfo(dependency)

    jsonReport.components.add([
        'type': 'library',
        'name': dependency.name,
        'version': dependency.version,
        'purl': "pkg:maven/${dependency.group}/${dependency.name}@${dependency.version}?packaging=jar",
        'licenses': [[
             'name': licenseInfo[1] ?: null,
            'url': licenseInfo[2],
        ]],
    ])
  })

  output.text = new JsonBuilder(jsonReport).toPrettyString()
}
}